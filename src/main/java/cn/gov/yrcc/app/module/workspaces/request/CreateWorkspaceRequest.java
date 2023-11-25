package cn.gov.yrcc.app.module.workspaces.request;

import lombok.Data;

@Data
public class CreateWorkspaceRequest {

    private String name;

    private boolean enable = true;
}
