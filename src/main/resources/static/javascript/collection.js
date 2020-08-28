var cash;

var freeCard;

var coinsInterval;

var freeCardInterval;

window.onclick = function(event) {
	if (event.target == document.getElementById("freeCardFragment"))
		closeFreeCardWindow();
}

function closeFreeCardWindow() {
	var toRemove = document.getElementById("freeCardFragment");
	document.getElementsByTagName("body")[0].removeChild(toRemove);
}

function collectFreeCard() {
	var supplier = {
		url: "/home/collect/freecard",
		apply: function(responseText) {
			document.getElementsByTagName("body")[0].insertAdjacentHTML("beforeend", responseText);
			document.getElementById("closeFreeCardButton").onclick = function(event) {
				closeFreeCardWindow();
			}
			getFreeCard();
		}
	};
	getRequest(supplier);
}

function getFreeCard() {
	var supplier = {
		url: "/home/get/freecard",
		apply: function(responseText) {
			freeCard = JSON.parse(responseText);
			setFreeCardTimer();
		}
	};
	getRequest(supplier);
}

function collectCoins() {
	var supplier = {
		url: "/home/collect/coins",
		apply: function(responseText) {
			cash = JSON.parse(responseText);
			document.getElementById("coins").innerHTML = cash.coins;
			setCoinsTimer();
		}
	};
	getRequest(supplier);
}

function getCash() {
	var supplier = {
		url: "/home/get/cash",
		apply: function(responseText) {
			cash = JSON.parse(responseText);
			setCoinsTimer();
		}
	};
	getRequest(supplier);
}

function getRequest(supplier) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200)
			supplier.apply(this.responseText);
  	};
	xhttp.open("GET", supplier.url, true);
	xhttp.send();
}

function setCoinsTimer() {
	var difference = cash.nextCoinsCollecting - Date.now();
	if (difference > 0) {
		disableCoinsButton();
		document.getElementById("chest").src = "images/closedChest.png";
		coinsInterval = setInterval(updateCoinsTime, 1_000);
		updateCoinsTime();
		return;
	}
	setActiveCoinsButton();
	difference += 24 * 3_600_000;
	if (difference > 0)
		setTimeout(getCash, difference);
}

function setFreeCardTimer() {
	var difference = freeCard.nextFreeCard - Date.now();
	if(difference > 0) {
		disableFreeCardButton();
		document.getElementById("pokeball").src = "images/redPokeball.png";
		freeCardInterval = setInterval(updateFreeCardTime, 1_000);
		updateFreeCardTime();
		return;
	}
	setActiveFreeCardButton();
}

function setActiveCoinsButton() {
	var element = document.getElementById("coinsButton");
	setActiveButton(element, cash.nextCoins, "images/coins.png", "Coins", 30, "margin-left:5px;", -25);
	document.getElementById("chest").src = "images/openedChest.png";
}

function setActiveFreeCardButton() {
	var element = document.getElementById("freeCardButton");
	setActiveButton(element, 1, "images/card.png", "Card", 16, "margin-top:-3px;", -38);
	document.getElementById("pokeball").src = "images/greenPokeball.png";
}

function setActiveButton(element, text, src, alt, size, style, margin) {
	var html = "<span class='float-left'>Collect</span>\n<span style='margin-left:";
	html += margin;
	html += "px;'>";
	html += text;
	html += "</span>\n";
	html += "<img style='"
	html += style;
	html += "' src='";
	html += src;
	html += "' alt='";
	html += alt;
	html +="' width='";
	html += size;
	html += "px'>\n";
	element.innerHTML = html;
	element.disabled = false;
}

function disableCoinsButton() {
	document.getElementById("coinsButton").disabled = true;
}

function disableFreeCardButton() {
	document.getElementById("freeCardButton").disabled = true;
}

function updateCoinsTime() {
	var element = document.getElementById("coinsButton");
	var difference = cash.nextCoinsCollecting - Date.now();
	if (difference <= 0) {
		clearInterval(coinsInterval);
		setCoinsTimer();
		return;
	}
	updateTime(difference, element)
}

function updateFreeCardTime() {
	var element = document.getElementById("freeCardButton");
	var difference = freeCard.nextFreeCard - Date.now();
	if (difference <= 0) {
		clearInterval(freeCardInterval);
		setActiveFreeCardButton();
		return;
	}
	updateTime(difference, element);
}

function updateTime(difference, element) {
	var diffDate = new Date(difference);
	var hours = diffDate.getUTCHours();
	var minutes = diffDate.getUTCMinutes();
	var seconds = diffDate.getUTCSeconds();
	element.innerHTML = appendZero(hours) + ":" + appendZero(minutes) + ":" + appendZero(seconds);
}

function formatCoins(coins) {
	var coinsString = coins + "";
}

function appendZero(value) {
	return value < 10 ? "0" + value : value
}