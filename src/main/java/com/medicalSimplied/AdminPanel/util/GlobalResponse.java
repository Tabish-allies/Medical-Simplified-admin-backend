package com.medicalSimplied.AdminPanel.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalResponse<T> {

    private Instant timestamp;       
    private int status;              
    private boolean success;         
    private String message;          
    private String path;             
    private T data;                  
    private List<String> errors;     
    
}
