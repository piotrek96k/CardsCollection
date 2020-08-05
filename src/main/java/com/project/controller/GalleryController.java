package com.project.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.model.repository.CardRepository;
import com.project.model.service.AccountService;

@Controller
public class GalleryController {

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private AccountService accountService;

	@GetMapping("/gallery")
	public String galleryPage(Model model, @RequestParam("page") Optional<Integer> page) {
		int currentPage = page.orElse(1);
		model.addAttribute("cards", accountService.getGalleryCards(currentPage));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("numberOfPages", cardRepository.getNumberOfPages());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("link", "/gallery");
		return "gallery";
	}

	@GetMapping("/gallery/buy")
	public String buyPage(Model model, @RequestParam("id") String id, @RequestParam("page") int page) {
		model.addAttribute("card",accountService.getQuantityCard(id));
		model.addAttribute("coins", accountService.getCoins());
		model.addAttribute("page", page);
		return "buy";
	}
	
	@GetMapping("/gallery/bought")
	public String boughtPage(@RequestParam("id") String id, @RequestParam("page") int page) {
		accountService.addCard(id);
		return "redirect:/gallery?page="+page;
	}
	
}