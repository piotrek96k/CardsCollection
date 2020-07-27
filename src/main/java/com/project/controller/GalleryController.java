package com.project.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.model.repository.CardRepository;

@Controller
public class GalleryController {

	@Autowired
	private CardRepository cardRepository;

	@GetMapping("/gallery")
	public String galleryPage(Model model, @RequestParam("page") Optional<Integer> page) {
		int currentPage = page.orElse(1);
		model.addAttribute("cards", cardRepository.getCardsByPageOrderByName(currentPage));
		model.addAttribute("numberOfPages", cardRepository.getNumberOfPages());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("link", "/gallery");
		return "gallery";
	}

}