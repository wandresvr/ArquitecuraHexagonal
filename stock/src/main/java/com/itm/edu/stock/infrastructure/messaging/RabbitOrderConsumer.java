package com.itm.edu.stock.infrastructure.messaging;

import com.itm.edu.common.dto.OrderMessageDTO;
import com.itm.edu.common.dto.events.StockValidationStatus;
import com.itm.edu.common.dto.events.StockUpdateResponseEvent;
import com.itm.edu.stock.application.ports.input.ProcessOrderUseCase;
import com.itm.edu.stock.domain.exception.BusinessException;
import com.itm.edu.stock.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitOrderConsumer {

    private final ProcessOrderUseCase processOrderUseCase;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
        queues = RabbitMQConfig.ORDER_QUEUE,
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void onOrderMessage(OrderMessageDTO msg) {
        if (msg == null) {
            throw new IllegalArgumentException("El mensaje de orden no puede ser nulo");
        }

        try {
            // Procesar la orden
            processOrderUseCase.processOrder(msg);
            
            // Crear el mensaje de respuesta
            StockUpdateResponseEvent response = new StockUpdateResponseEvent();
            response.setOrderId(msg.getOrderId());
            response.setStatus(StockValidationStatus.RESERVED);

            // Enviar la respuesta al exchange de respuestas
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.STOCK_RESPONSE_EXCHANGE,
                RabbitMQConfig.STOCK_RESPONSE_ROUTING_KEY,
                response
            );

            log.info("Orden {} procesada. Stock reservado.", msg.getOrderId());
        } catch (BusinessException e) {
            log.error("Error de negocio procesando la orden {}: {}", msg.getOrderId(), e.getMessage());
            
            // Enviar respuesta de error
            StockUpdateResponseEvent response = new StockUpdateResponseEvent();
            response.setOrderId(msg.getOrderId());
            response.setStatus(StockValidationStatus.CANCELLED_NO_STOCK);

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.STOCK_RESPONSE_EXCHANGE,
                RabbitMQConfig.STOCK_RESPONSE_ROUTING_KEY,
                response
            );

            // No relanzamos la excepción para que el mensaje no se reintente
            log.warn("Orden {} cancelada. Motivo: {}", msg.getOrderId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado procesando la orden {}: {}", msg.getOrderId(), e.getMessage(), e);
            
            // Para errores del sistema también usamos CANCELLED_NO_STOCK
            StockUpdateResponseEvent response = new StockUpdateResponseEvent();
            response.setOrderId(msg.getOrderId());
            response.setStatus(StockValidationStatus.CANCELLED_NO_STOCK);

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.STOCK_RESPONSE_EXCHANGE,
                RabbitMQConfig.STOCK_RESPONSE_ROUTING_KEY,
                response
            );

            throw new AmqpRejectAndDontRequeueException("Error inesperado: " + e.getMessage(), e);
        }
    }
}
