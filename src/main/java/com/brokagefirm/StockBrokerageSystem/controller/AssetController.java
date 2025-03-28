package com.brokagefirm.StockBrokerageSystem.controller;

import com.brokagefirm.StockBrokerageSystem.dto.AssetDTO;
import com.brokagefirm.StockBrokerageSystem.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Assets API", description = "API for managing assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/list")
    @Operation(
            summary = "List all assets for a customer",
            description = "Retrieves a list of assets related to the customer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched assets"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid customer ID")
    })
    @Schema(description = "List assets by customer ID")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<AssetDTO> listAssets(@RequestParam Long customerId) {
        return assetService.listAssets(customerId);
    }
}
