package cn.gov.yrcc.app.module.datastores.controller;

import cn.gov.yrcc.app.database.schema.Datastore;
import cn.gov.yrcc.app.module.datastores.service.DatastoreService;
import cn.gov.yrcc.internal.geoserver.entity.GSDatastore;
import cn.gov.yrcc.utils.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "存储仓库")
@RestController
@RequestMapping("/v1")
public class DatastoreController {

    private final DatastoreService datastoreService;

    public DatastoreController(DatastoreService datastoreService) {
        this.datastoreService = datastoreService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Request failed")
    })
    @Operation(summary = "检查存储仓库是否存在", method = "GET")
    @GetMapping("datastore/exists")
    public BaseResult<Boolean> existsDelivery(
            @Parameter(description = "工作空间名称", in = ParameterIn.QUERY) @RequestParam("workspaceName") String workspaceName,
            @Parameter(description = "存储仓库名称", in = ParameterIn.QUERY)@RequestParam("datastoreName") String datastoreName) {
        return BaseResult.success(datastoreService.exists(workspaceName, datastoreName));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Request failed")
    })
    @Operation(summary = "存储仓库列表", method = "GET")
    @GetMapping("datastore/list")
    public BaseResult<Object> listDelivery(
            @Parameter(description = "工作空间名称", in = ParameterIn.QUERY) @RequestParam("workspaceName") String workspaceName) {
        return BaseResult.success(datastoreService.list(workspaceName));
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "存储仓库详情", method = "GET")
    @GetMapping("datastore/details")
    public BaseResult<GSDatastore.Datastore> detailsDelivery(
            @Parameter(description = "工作空间名称", in = ParameterIn.QUERY) @RequestParam("workspaceName") String workspaceName,
			@Parameter(description = "存储仓库名称", in = ParameterIn.QUERY) @RequestParam("datastoreName") String datastoreName) {
        return BaseResult.success(datastoreService.details(workspaceName, datastoreName));
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "分页查询存储仓库", method = "POST")
    @PostMapping("datastore/pages/{page}/{size}")
    public BaseResult<Page<Datastore>> pages(
		@Parameter(description = "页码", in = ParameterIn.PATH) @PathVariable("page") Integer page,
		@Parameter(description = "行数", in = ParameterIn.PATH) @PathVariable("size") Integer size) {
        return BaseResult.success(datastoreService.pages(page, size));
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "启用存储仓库", method = "PUT")
    @PutMapping("datastore/enable")
    public BaseResult<Void> enableDelivery(
		@Parameter(description = "主键", in = ParameterIn.QUERY) @RequestParam("id") Long id) {
        datastoreService.enable(id);
        return BaseResult.success();
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "禁用存储仓库", method = "PUT")
    @PutMapping("datastore/disable")
    public BaseResult<Void> disableDelivery(
		@Parameter(description = "主键", in = ParameterIn.QUERY)  @RequestParam("id") Long id) {
        datastoreService.disable(id);
        return BaseResult.success();
    }

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successful operation"),
		@ApiResponse(responseCode = "400", description = "Request failed")
	})
	@Operation(summary = "删除存储仓库", method = "DELETE")
    @DeleteMapping("datastore/delete")
    public BaseResult<Void> deleteDelivery(
		@Parameter(description = "主键", in = ParameterIn.QUERY) @RequestParam("id") Long id) {
        datastoreService.delete(id);
        return BaseResult.success();
    }
}
