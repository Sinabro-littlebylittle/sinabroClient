package com.project.sinabro.models;

import java.util.List;

public class CoordinateToAddress {
    private Meta meta;
    private List<Document> documents;

    // getters and setters for 'meta' and 'documents'
    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public static class Meta {
        private int total_count;

        // getter and setter for 'total_count'
        public int getTotalCount() {
            return total_count;
        }

        public void setTotalCount(int total_count) {
            this.total_count = total_count;
        }
    }

    public static class Document {
        private RoadAddress road_address;
        private Address address;

        // getters and setters for 'road_address' and 'address'
        public RoadAddress getRoadAddress() {
            return road_address;
        }

        public void setRoadAddress(RoadAddress road_address) {
            this.road_address = road_address;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class RoadAddress {
        private String address_name;
        // ... other fields ...

        // getter and setter for 'address_name'
        public String getAddressName() {
            return address_name;
        }

        public void setAddressName(String address_name) {
            this.address_name = address_name;
        }

        // ... other getters and setters ...
    }

    public static class Address {
        private String address_name;
        // ... other fields ...

        // getter and setter for 'address_name'
        public String getAddressName() {
            return address_name;
        }

        public void setAddressName(String address_name) {
            this.address_name = address_name;
        }

        // ... other getters and setters ...
    }
}
