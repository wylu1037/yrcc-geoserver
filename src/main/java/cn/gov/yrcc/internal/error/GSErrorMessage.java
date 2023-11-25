package cn.gov.yrcc.internal.error;

public class GSErrorMessage {

    private GSErrorMessage() {
    }

    public static class Workspace {
        private Workspace() {
        }

        public static final String NOT_EXISTS = "工作空间不存在";

        public static final String ALREADY_EXISTS = "工作空间已存在";
    }

    public static class Datastore {
        private Datastore() {
        }

        public static final String NOT_EXISTS = "存储仓库不存在";
    }
}
