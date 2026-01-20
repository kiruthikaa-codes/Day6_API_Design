package com.tekion.API_Design_Demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data 
@Schema(description = "Data Transfer Object for Product information")
public class ProductDTO {
    
    @Schema(description = "Unique product id", example ="prod-101")
    private String productId;

    @Schema(description = "Product name", example ="Smartphone")
    private String name;

    @Schema(description = "product description", example ="Latest model smartphone with advanced features")
    private String description;

    @Schema(description = "Product price in USD", example ="699.99")
    private double price;   

    @Schema(description = "product category", example = "electronics")
    private String category;
}

