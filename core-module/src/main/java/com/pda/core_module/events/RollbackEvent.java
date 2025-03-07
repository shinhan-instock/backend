package com.pda.core_module.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RollbackEvent implements Serializable {
    private Long postId;
    private String correlationId;
    private String reason;
}
