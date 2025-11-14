package com.bolao.brasileirao.dtos;

import lombok.Data;

import java.util.List;

@Data
public class FixtureResponse {
    private List<FixtureItem> response;

    @Data
    public static class FixtureItem {
        private Fixture fixture;
        private Teams teams;
        private Goals goals;

        @Data
        public static class Fixture {
            private int id;
            private String date;
            private Status status;
            private Venue venue;

            @Data public static class Status { private String shortName; }
            @Data public static class Venue { private String name; }
        }

        @Data
        public static class Teams {
            private Team home;
            private Team away;

            @Data
            public static class Team {
                private String name;
                private String code;
            }
        }

        @Data
        public static class Goals {
            private Integer home;
            private Integer away;
        }
    }
}


