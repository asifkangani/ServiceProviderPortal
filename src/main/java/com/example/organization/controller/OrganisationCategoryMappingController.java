package com.example.organization.controller;

import com.example.organization.service.iface.OrganisationFormsFieldsConfigureIface;
import com.example.organization.util.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/public")
@RestController
public class OrganisationCategoryMappingController {
    private static final String CLASS = "OrganisationCategoryMappingController";
    private static final Logger logger = LoggerFactory.getLogger(OrganisationCategoryMappingController.class);

    @Autowired
    OrganisationFormsFieldsConfigureIface organisationFormsFieldsConfigureIface;




    @GetMapping("/get/all/categories")
    public ApiResponse getCategories(){
        logger.info("{} get all Categories ",CLASS);
        return organisationFormsFieldsConfigureIface.getAllCategories();
    }




}
