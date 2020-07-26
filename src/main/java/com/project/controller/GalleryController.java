package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.model.service.ApiService;

@Controller
public class GalleryController {
	
	@Autowired
	private ApiService loader;
	
	@GetMapping("/gallery")
	public String galleryPage(Model model) {
		model.addAttribute("cards", loader.getCardsByPage(1));
		return "gallery";
	}
	
}