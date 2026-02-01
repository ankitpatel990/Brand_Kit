package com.brandkit.customization.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
/**
 * Crop Data Request DTO
 * FRD-003: Crop coordinates and zoom level
 */
public class CropDataRequest {

    @NotNull(message = "X coordinate is required")
    @Min(value = 0, message = "X coordinate must be non-negative")
    private Double x;

    @NotNull(message = "Y coordinate is required")
    @Min(value = 0, message = "Y coordinate must be non-negative")
    private Double y;

    @NotNull(message = "Width is required")
    @Min(value = 100, message = "Width must be at least 100px")
    private Double width;

    @NotNull(message = "Height is required")
    @Min(value = 100, message = "Height must be at least 100px")
    private Double height;

    @NotNull(message = "Zoom level is required")
    @Min(value = 1, message = "Zoom must be at least 1x")
    private Double zoom;

    @NotNull(message = "Aspect ratio is required")
    private Double aspectRatio;

    public Double getX() {
        return this.x;
    }
    public Double getY() {
        return this.y;
    }
    public Double getWidth() {
        return this.width;
    }
    public Double getHeight() {
        return this.height;
    }
    public Double getZoom() {
        return this.zoom;
    }
    public Double getAspectRatio() {
        return this.aspectRatio;
    }
    public void setX(Double x) {
        this.x = x;
    }
    public void setY(Double y) {
        this.y = y;
    }
    public void setWidth(Double width) {
        this.width = width;
    }
    public void setHeight(Double height) {
        this.height = height;
    }
    public void setZoom(Double zoom) {
        this.zoom = zoom;
    }
    public void setAspectRatio(Double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    public CropDataRequest() {
    }
    public CropDataRequest(Double x, Double y, Double width, Double height, Double zoom, Double aspectRatio) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        this.aspectRatio = aspectRatio;
    }
    public static CropDataRequestBuilder builder() {
        return new CropDataRequestBuilder();
    }

    public static class CropDataRequestBuilder {
        private Double x;
        private Double y;
        private Double width;
        private Double height;
        private Double zoom;
        private Double aspectRatio;

        CropDataRequestBuilder() {
        }

        public CropDataRequestBuilder x(Double x) {
            this.x = x;
            return this;
        }

        public CropDataRequestBuilder y(Double y) {
            this.y = y;
            return this;
        }

        public CropDataRequestBuilder width(Double width) {
            this.width = width;
            return this;
        }

        public CropDataRequestBuilder height(Double height) {
            this.height = height;
            return this;
        }

        public CropDataRequestBuilder zoom(Double zoom) {
            this.zoom = zoom;
            return this;
        }

        public CropDataRequestBuilder aspectRatio(Double aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public CropDataRequest build() {
            CropDataRequest instance = new CropDataRequest();
            instance.x = this.x;
            instance.y = this.y;
            instance.width = this.width;
            instance.height = this.height;
            instance.zoom = this.zoom;
            instance.aspectRatio = this.aspectRatio;
            return instance;
        }
    }
}
