function buyCard(id) {
	$.ajax({
		type:"post",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		url: "/buy/one/" + id,
		async: true,
		dataType: "json",
		success: function(response) {
			setNewValuesFromJson(response, id);
			disableButtonsIfNeed(parseInt(response.coins.replaceAll(" ", "")));
		},
	});
}

function buyAll(page, rarity, set, type, search) {
	$.ajax({
		type:"post",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		url: getUrl("/buy/all" ,page, rarity, set, type, search).path,
		async: true,
		dataType: "html",
		success: function(response) {
			switchCardsPageBody(response);
		},
	});
}

function sellCard(id ,page, rarity, set, type, search) {
	var path = "/sell";
	$.ajax({
		type:"post",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		url: getUrl(path + "/one/" + id, page, rarity, set, type, search).path,
		async: true,
		dataType: "json",
		success: function(response) {
			setNewValuesFromJson(response, id);
			document.getElementById("sellAllCoins").innerHTML = response.totalValue;
			if (response.quantity == "0"){
				document.getElementById(id).disabled = true;
				setCardsFragment(path, page, rarity, set, type, search);
			}
		},
	});
}

function sellAll(page, rarity, set, type, search) {
	$.ajax({
		type:"post",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		url: getUrl("/sell/all", page, rarity, set, type, search).path,
		async: true,
		dataType: "html",
		success: function(response) {
			switchCardsPageBody(response);
		},
	});
}

function switchCardsPageBody(response) {
	var html = new DOMParser().parseFromString(response, "text/html");
	var scroll = document.getElementById("verticalmenu").scrollTop;
	document.body = html.body; 
	installListeners(document.getElementById("verticalmenu").scrollTop = scroll);
}

function setNewValuesFromJson(data, id) {
	document.getElementById("coins").innerHTML = data.coins;
	document.getElementById("quantity" + id).innerHTML = data.quantity;
	document.getElementById(data.rarity.id + "userQuantity").innerHTML = data.rarity.userQuantity;
	document.getElementById(data.set.id + "userQuantity").innerHTML = data.set.userQuantity;	
	for (let i = 0; i < data.types.length; i++)
		document.getElementById(data.types[i].id + "userQuantity").innerHTML = data.types[i].userQuantity;
}

function setCardsFragment(path ,page, rarity, set, type, search) {
	var url = getUrl(path ,page, rarity, set, type, search);
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
			var html = new DOMParser().parseFromString(this.responseText, "text/html");
			var cards = html.getElementsByName("sellbutton").length;
			if (cards > 0 || page == 1) {
				document.getElementById("cardpage").innerHTML = html.getElementById("cardpage").innerHTML;
				document.getElementById("pagination").innerHTML = html.getElementById("pagination").innerHTML;
				document.getElementById("numberOfCards").innerHTML = html.getElementById("numberOfCards").innerHTML;
				if(document.getElementById("numberOfCards").innerHTML === "1")
					document.getElementById("cardsFound").innerHTML = "Card Found";
				else
					document.getElementById("cardsFound").innerHTML = "Cards Found";
				installTooltipListeners();
			}
			else if (page > 1)
				if (page == 2)
					window.location.href = getUrl(path, null, rarity, set, type, search).path;
				else
					window.location.href = getUrl(path, page-1, rarity, set, type, search).path;
		}
  	};
	xhttp.open("GET", url.path, true);
	xhttp.send();
}

function setHomeSearch() {
	var element = document.getElementById("search");
	if(element.value != "") {
		$.ajax({
			type:"get",
			headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
			data: {search: element.value},
			url: "/home",
			async: true,
			dataType: "html",
			success: function(response) {
				setHomeCardsFragmentOnSuccess(response);
				window.history.pushState({"html":document.html,"pageTitle":document.pageTitle},"", "?search=" + encodeURIComponent(element.value));
			},
		});
	}
	else
		resetIndexSearch(element);
}

function resetHomeSearch(element) {
	$.ajax({
		type:"get",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		url: "/home",
		async: true,
		dataType: "html",
		success: function(response) {
			setHomeCardsFragmentOnSuccess(response);
			window.history.pushState({"html":document.html,"pageTitle":document.pageTitle},"", "/");
		},
	});
}

function setHomeCardsFragment() {
	var cards = document.getElementsByClassName("card-fragment");
	var ids = new Array();
	for(let i = 0; i < 5; i++) 
		ids.push(String(cards[i].id).substring(3));
	$.ajax({
		type:"get",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		data: {ids: ids},
		url: window.location.href,
		async: true,
		dataType: "html",
		success: function(response) {
			setHomeCardsFragmentOnSuccess(response);
		},
	});
}

function setHomeCardsFragmentOnSuccess(response) {
	var html = new DOMParser().parseFromString(response, "text/html");
	document.getElementById("cards").innerHTML = html.getElementById("cards").innerHTML;
	installTooltipListeners();
}

function getUrl(path ,page, rarity, set, type, search) {
	var url = {
		path: path,
		added: false,
	};
	appendUrl(url, "page", page);
	appendUrl(url, "rarity", rarity);
	appendUrl(url, "set", set);
	appendUrl(url, "type", type);
	appendUrl(url, "search", search);
	return url;
}

function appendUrl(url, name, data) {
	if (data != null && data != "") {
		if (url.added)
			url.path += "&";
		else
			url.path += "?";
		var data = encodeURIComponent(data.toString());
		url.added = true;
		url.path += name;
		url.path += "=";
		url.path += data;
	}
}

function disableButtonsIfNeed(coins) {
	var buttons = document.getElementsByName("buybutton");
	for (let i = 0; i < buttons.length; i++){
		let id = buttons[i].id;
		let cost = parseInt(document.getElementById("cost" + id).innerHTML.replace(" ", ""));
		buttons[i].disabled = coins < cost;
	}
}

function installListeners(scroll) {
	installScrollListener(scroll);
	installExpandListeners();
	installTooltipListeners();
}

function installTooltipListeners() {
	var coll = document.getElementsByClassName("tool-tip");
	for (let i = 0; i<coll.length; i++) {
		coll[i].addEventListener("mouseenter", item=>{
			var height = coll[i].getElementsByTagName("img")[0].clientHeight;
			var element = coll[i].getElementsByClassName("tooltip-table")[0];
			element.style.height = height + "px";
			var location = coll[i].getBoundingClientRect();
			if (window.innerWidth > location.right + element.clientWidth + 25)
				element.style.marginLeft = "0px";
			else
				element.style.marginLeft =  - coll[i].clientWidth - element.clientWidth + "px";
		});
	}
}

function installScrollListener(scroll) {
	var verticalMenu = document.getElementById("verticalmenu");
	verticalMenu.scrollTop = scroll;
	verticalMenu.addEventListener("scroll", item=>{
		var scroll = verticalMenu.scrollTop;
		$.ajax({
			type:"post",
			headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
			data: {scroll : scroll},
			url:"/scroll",
			async: true,
			dataType: "text",
		});
	});
}

function installExpandListeners() {
	var coll = document.getElementsByClassName("vertical-menu-top");
	for (let i = 0; i < coll.length; i++) {
		let element = coll[i];
		element.addEventListener("click", item => {
			var content = element.nextElementSibling;
			if (content.style.display === "none") {
				element.getElementsByTagName("img")[0].src = "images/hide.png";
				content.style.display = "block";
			} else {
				element.getElementsByTagName("img")[0].src = "images/expand.png";
				content.style.display = "none";
			}
			sendExpandData(element.getElementsByTagName("button")[0].id);
		});
	}
}

function sendExpandData(id) {
	$.ajax({
		type:"post",
		headers: {"X-CSRF-TOKEN": $("input[name='_csrf']").val()},
		data: {expand : id},
		url:"/expand",
		async: true,
		dataType: "text",
	});
}

function changeDropdownColor() {
	document.getElementById("dropdown").style.backgroundColor = "#565656";
}

function returnDropdownColor() {
	document.getElementById("dropdown").style.backgroundColor = "#969390";
}

function eraseSearch() {
	document.getElementById("search").value = "";
}