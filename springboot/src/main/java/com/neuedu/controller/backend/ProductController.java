package com.neuedu.controller.backend;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.RoleEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.User;
import com.neuedu.service.IProductService;
import com.neuedu.utils.Const;
import com.neuedu.vo.ProductDetailVO;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/manage/product")
public class ProductController  {

    @Autowired
    IProductService productService;
    /**
     * 商品添加&更新
     */
    @RequestMapping("/save.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse addOrUpdate(Product product, HttpSession session){


        return productService.addOrUpdate(product);
    }

    /**
     * 商品上下架
     */
@RequestMapping("/updateStatu/{productId}/{status}")
    public ServerResponse updateStatu(@PathVariable("productId") Integer productId,@PathVariable("status") Integer status, HttpSession session){

        return productService.updateStatu(productId,status);
    }

    /**
     * 搜索商品
     *@RequestParam(name = "productName",required = false) required表示参数可传可不传
     */

    @RequestMapping(value = "search.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse search(@RequestParam(name = "productName",required = false) String productName,
                                 @RequestParam(name = "productId",required = false) Integer productId,
                                 @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return productService.search(productName, productId, pageNum, pageSize);
    }

    @RequestMapping(value = "/{productId}")
    public ServerResponse detail(@PathVariable("productId") Integer productId){
        return productService.detail(productId);
    }

    //查询所有下架商品
    @RequestMapping(value = "findProductDown.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse findProductDown(@RequestParam(name = "productName",required = false) String productName,
                                          @RequestParam(name = "productId",required = false) Integer productId,
                                          @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return productService.findProductDown(productName, productId, pageNum, pageSize);
    }

    //excel导出商品列表
   /* @RequestMapping(value = "exportProduct.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public void exportProduct(HttpServletResponse response) throws IOException {
        String[] header = {"ID","类别ID","父类ID","商品名","别名","图片host路径","主图","所有图片","商品描述","价格","库存","状态","创建时间","修改时间"};
        List<ProductDetailVO> products = productService.findAllProduct();
        ProductDetailVO p = new ProductDetailVO();
        //声明一个工作铺
        HSSFWorkbook workbook = new HSSFWorkbook();
        //生成一个表格，设置表格名称为：“商品表”
        HSSFSheet sheet = workbook.createSheet("商品表");
        //设置表格列宽度为10个字节
        sheet.setDefaultColumnWidth(10);
        HSSFRow headRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            //创建一个单元格
            HSSFCell cell = headRow.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(header[i]);
            cell.setCellValue(text);
        }
        for (int i = 1; i < products.size()+1; i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < header.length; j++) {
                HSSFCell cell = row.createCell(j);
                HSSFRichTextString text = new HSSFRichTextString();
                cell.setCellValue(text);
            }
        }
        //准备将Excel的输出流通过response输出到页面下载
        //八进制输出流
        response.setContentType("application/octet-stream");
        //这后面可以设置导出Excel的名称，此例中名为Product.xls
        response.setHeader("Content-disposition", "attachment;filename=Product.xls");
        //刷新缓冲
        response.flushBuffer();

        //workbook将Excel写入到response的输出流中，供页面下载
        workbook.write(response.getOutputStream());
    }
*/


}
