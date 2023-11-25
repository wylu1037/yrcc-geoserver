package cn.gov.yrcc.internal.geoserver.entity;

import lombok.Data;

@Data
public class GSDatastore {

    private Datastore dataStore;

    @Data
    public static class Datastore {
        private String name;
        private String type;
        private Boolean enabled;
        private Workspace workspace;
        private String dateCreated;
        private String dateModified;

        @Data
        public static class Workspace {
            private String name;
            private String href;
        }
    }
}
