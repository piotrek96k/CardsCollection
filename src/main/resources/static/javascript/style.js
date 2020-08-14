function buyCard(id) {
	var xhttp = new XMLHttpRequest();
	var url = "/gallery/buy?id=" + id;
	xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	var data = JSON.parse(this.responseText);
		document.getElementById("coins").innerHTML = data.coins;
		document.getElementById("quantity" + id).innerHTML = "x" + data.quantity;
		disableButtonsIfNeed(parseInt(data.coins.replace(" ", "")));
    }
  };
	xhttp.open("GET", url, true);
	xhttp.send();
}

function disableButtonsIfNeed(coins){
	var buttons = document.getElementsByName("buybutton");
	var sum = 0;
	for(let i = 0; i < buttons.length; i++){
		let id = buttons[i].id;
		let cost = parseInt(document.getElementById("cost" + id).innerHTML.replace(" ", ""));
		buttons[i].disabled = coins < cost;
	}
}

function installExpandListener(){
	var coll = document.getElementsByClassName("vertical-menu-top");
	for (let i = 0; i < coll.length; i++) {
		let element = coll[i];
		element.addEventListener("click", item=> {
			var content = element.nextElementSibling;
			if (content.style.display === "none") {
				element.getElementsByTagName("IMG")[0].src = "images/hide.png";
				element.style.marginBottom="0px";
				content.style.display = "block";
			} else {
				element.getElementsByTagName("IMG")[0].src = "images/expand.png";
				element.style.marginBottom="10px";
				content.style.display = "none";
			}
		});
	}
}

function changeDropdownColor(){
	document.getElementById("dropdown").style.backgroundColor="#565656";
}

function returnDropdownColor(){
	document.getElementById("dropdown").style.backgroundColor="#969390";
}

function eraseSearch(){
	document.getElementById("search").value ="";
}