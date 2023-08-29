package org.transactionService.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * This class is used as a data transfer object for creating transaction.
 *
 * @author safwanmohammed907@gmal.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequest {
    @NotBlank(message = "Receiver is mandatory.")
    private String receiver;
    @NotBlank(message = "Sender is mandatory.")
    private String sender;
    @Min(value = 1, message = "Amount must be at least 1 rupees")
    private Long amount;
    private String reason;
}
