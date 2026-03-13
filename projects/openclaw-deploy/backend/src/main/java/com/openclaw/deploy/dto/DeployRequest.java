package com.openclaw.deploy.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeployRequest {
    private String plan;
    private String email;
    private String notes;
    private List<String> skills;
}