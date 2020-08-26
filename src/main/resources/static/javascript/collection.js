var cash;
var interval;

function collectCoins() {
	var xhttp = new XMLHttpRequest();
	var url = "/home/collect/coins";
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
			cash = JSON.parse(this.responseText);
			document.getElementById("coins").innerHTML = cash.coins;
			setTimer();
		}
  	};
	xhttp.open("GET", url, true);
	xhttp.send();
}

function getCash() {
	var xhttp = new XMLHttpRequest();
	var url = "/home/get/cash";
	xhttp.onreadystatechange = function() {
  		if (this.readyState == 4 && this.status == 200) {
			cash = JSON.parse(this.responseText);
			setTimer();
		}
  	};
	xhttp.open("GET", url, true);
	xhttp.send();
}

function setTimer() {
	var difference = cash.nextCoinsCollecting - Date.now();
	if (difference > 0) {
		disableCoinsButton();
		document.getElementById("chest").src = "images/closedChest.png"
		interval = setInterval(updateTime, 1_000);
		updateTime();
		return;
	}
	document.getElementById("chest").src = "images/openedChest.png"
	setCoinsButton();
	difference += 24 * 3_600_000;
	if (difference > 0)
		setTimeout(getCash, difference);
}

function setCoinsButton() {
	var element = document.getElementById("coinsButton");
	var html = "<span class='float-left'>Collect</span>\n<span style='margin-left:-25px;'>";
	html += cash.nextCoins;
	html += "</span>\n";
	html += "<img style='margin-left:5px;' src='images/coins.png' alt='Coins' width='30px'>\n";
	element.innerHTML = html;
	element.disabled = false;
}

function disableCoinsButton() {
	document.getElementById("coinsButton").disabled = true;
}

function updateTime() {
	var element = document.getElementById("coinsButton");
	var difference = cash.nextCoinsCollecting - Date.now();
	if (difference <= 0) {
		clearInterval(interval);
		setTimer();
		return;
	}
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