function buyCard(id) {
	var xhttp = new XMLHttpRequest();
	var url = "/gallery/buy?id=" + id;
	xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	var data = JSON.parse(this.responseText);
		document.getElementById("coins").innerHTML = data.coins;
		document.getElementById("quantity" + id).innerHTML = "x" + data.quantity;
		disableButtonsIfNeed(parseInt(data.coins.replaceAll(" ", "")));
    }
  };
	xhttp.open("GET", url, true);
	xhttp.send();
}

function disableButtonsIfNeed(coins){
	var buttons = document.getElementsByName("buybutton");
	for (let i = 0; i < buttons.length; i++){
		let id = buttons[i].id;
		let cost = parseInt(document.getElementById("cost" + id).innerHTML.replace(" ", ""));
		buttons[i].disabled = coins < cost;
	}
}

function installListeners(){
	installExpandListeners();
	installTooltipListeners();
}

function installTooltipListeners(){
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

function installExpandListeners(){
	var coll = document.getElementsByClassName("vertical-menu-top");
	for (let i = 0; i < coll.length; i++) {
		let element = coll[i];
		element.addEventListener("click", item=> {
			var content = element.nextElementSibling;
			if (content.style.display === "none") {
				element.getElementsByTagName("img")[0].src = "images/hide.png";
				content.style.display = "block";
			} else {
				element.getElementsByTagName("img")[0].src = "images/expand.png";
				content.style.display = "none";
			}
		});
	}
}

function changeDropdownColor(){
	document.getElementById("dropdown").style.backgroundColor = "#565656";
}

function returnDropdownColor(){
	document.getElementById("dropdown").style.backgroundColor = "#969390";
}

function eraseSearch(){
	document.getElementById("search").value = "";
}