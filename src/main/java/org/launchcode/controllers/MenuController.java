package org.launchcode.controllers;


import org.launchcode.models.Cheese;
import org.launchcode.models.Data.CheeseDao;
import org.launchcode.models.Data.MenuDao;
import org.launchcode.models.Menu;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    private String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    private String addMenu(Model model) {

        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    private String processAddMenuForm(@ModelAttribute @Valid Menu menu, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            model.addAttribute("menus", menuDao.findAll());
            return "menu/add";
        }
            //TODO - this is likely incorrect and needs a newMenu object handed off, I will come back to it
            menuDao.save(menu);

            return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{Id}")
    private String viewMenu(@PathVariable("Id") int Id, Model model){

        Menu a_menu = menuDao.findOne(Id);
        model.addAttribute("title", a_menu.getName());
        model.addAttribute("menu", a_menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{Id}", method = RequestMethod.GET)
    private String addItem(@PathVariable("Id") int Id, Model model){

        Menu a_menu = menuDao.findOne(Id);
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm();
        addMenuItemForm.setMenu(a_menu);
        addMenuItemForm.setCheeses(cheeseDao.findAll());
        model.addAttribute("form", addMenuItemForm);
        model.addAttribute("title", "Add item to menu " + a_menu.getName());
        model.addAttribute("menu", a_menu);

        return "menu/add-item";
    }
    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    private String addItem(@ModelAttribute @Valid AddMenuItemForm addMenuItemForm, Errors errors, Model model){

        if (errors.hasErrors()) {
            //this might not work as it stands, will reconsider upon testing
            return "menu/add-item";
        }
        //addMenuItemForm.setCheeses(cheeseDao.findAll());
        //Cheese theCheese = cheeseDao.findOne( [addMenuItemForm.getCheeseId()]);
        Cheese theCheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        Menu theMenu = menuDao.findOne(addMenuItemForm.getMenuId());
        theMenu.getCheeses().add(theCheese);
        menuDao.save(theMenu);
        return "redirect:view/" + theMenu.getId();
    }

}
