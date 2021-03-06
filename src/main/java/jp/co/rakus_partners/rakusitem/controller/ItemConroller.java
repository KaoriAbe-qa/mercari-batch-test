package jp.co.rakus_partners.rakusitem.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.co.rakus_partners.rakusitem.entity.Category;
import jp.co.rakus_partners.rakusitem.entity.Item;
import jp.co.rakus_partners.rakusitem.form.SearchForm;
import jp.co.rakus_partners.rakusitem.service.CategoryService;
import jp.co.rakus_partners.rakusitem.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemConroller {

    public static final int ROW_PAR_PAGE = 30;

    @Autowired
    private HttpSession session;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute
    private SearchForm setupForm() {
        return new SearchForm();
    }

    @RequestMapping("/search")
    public String search(SearchForm searchForm, Model model) {
        if (searchForm.getPage() == null) {
            searchForm.setPage(1);
        }
        Integer searchCount = itemService.searchCount(searchForm);
        int maxPage = searchCount / ROW_PAR_PAGE;
        if (searchCount % ROW_PAR_PAGE != 0) {
            maxPage++;
        }
        if (searchForm.getPage() > maxPage) {
            searchForm.setPage(1);
        }

        setCategoryIds(searchForm, getCategories());

        model.addAttribute("itemList", itemService.search(searchForm));
        model.addAttribute("maxPage", maxPage);
        return "list.html";
    }

    @RequestMapping("/categories")
    @ResponseBody
    public List<Category> categories() {
        return getCategories();
    }

    /**
     * ???????????????????????????????????????.
     * ??????????????????????????????????????????????????????????????????DB??????????????????.
     *
     * @return
     */
    private List<Category> getCategories() {
        List<Category> categories = (List<Category>) session.getAttribute("categories");
        if (categories == null) {
            categories = categoryService.findAllCategories();
            session.setAttribute("categories", categories);
        }
        return categories;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     * categoryName?????????daiCategoryId, chuCategoryId, syoCategoryId ??????????????????????????????????????????.
     *
     * @param searchForm
     * @param categories
     */
    private void setCategoryIds(SearchForm searchForm, List<Category> categories) {
        // ??????????????????????????????
        searchForm.setDaiCategoryId(null);
        searchForm.setChuCategoryId(null);
        searchForm.setSyoCategoryId(null);
        if (searchForm.getCategoryName() != null) {
            String[] categoryArray = searchForm.getCategoryName().split("/");
            if (categoryArray.length >= 1 && !"".equals(categoryArray[0])) {
                Category daiCategory = getCategoryByName(categories, categoryArray[0]);
                searchForm.setDaiCategoryId(daiCategory.getId());
                if (categoryArray.length >= 2) {
                    Category chuCategory = getCategoryByName(daiCategory.getChildCategories(), categoryArray[1]);
                    searchForm.setChuCategoryId(chuCategory.getId());
                    if (categoryArray.length >= 3) {
                        Category syoCategory = getCategoryByName(chuCategory.getChildCategories(), categoryArray[2]);
                        searchForm.setSyoCategoryId(syoCategory.getId());
                    }
                }
            }
        }
    }

    private Category getCategoryByName(List<Category> categories, String categoryName) {
        for (Category category : categories) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }
    
    
    
    
    /**
     * CsvMapper???csv?????????tsv??????????????????
     */ 
    @RequestMapping("/csvDownload")
    public String csvDownload(HttpServletResponse response) throws JsonProcessingException{
    	System.out.println("URL??????????????????");
    	
    	//??????????????????????????????CSV????????????????????????
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE + ";charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"test.tsv\"");
      
        try (PrintWriter pw = response.getWriter()) {
       
        	//DB??????????????????????????????
        	List<Item> items = new ArrayList<Item>();
        	items = itemService.searchAll();
        
        	//"ID", "??????", "??????", "???????????????", "????????????", "??????", "??????", "??????"
        	for (int i = 0; i < items.size(); i++) {
        		long id = items.get(i).getId();
        		String name = items.get(i).getName();
        		Integer condition = items.get(i).getCondition();
        		Integer category = items.get(i).getCategory();
        		String brand = items.get(i).getBrand();
        		Integer price = items.get(i).getPrice();
        		Integer shipping = items.get(i).getShipping();
        		String description = items.get(i).getDescription();

        		//CSV????????????????????????????????????????????????????????????
        		String outputString = name + "\t" + condition + "\t" + category
            		+ "\t" + brand + "\t" + price + "\t" + shipping + "\t" + description
                     + "\r\n";
        		
        		//ID??????Ver
        		//String outputString = id + "\t" + name + "\t" + condition + "\t" + category
        		//+ "\t" + brand + "\t" + price + "\t" + shipping + "\t" + description
                //+ "\r\n";
        		

        		//CSV???????????????????????????
        		pw.print(outputString);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("tsv????????????");
        return "list.html";
    }
    

    
}

