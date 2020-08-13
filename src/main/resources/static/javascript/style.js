function expand(){
	var coll = document.getElementsByClassName("vertical-menu-top");
	for (let i = 0; i < coll.length; i++) {
		let element = coll[i];
		element.addEventListener("click", function() {
			var content = this.nextElementSibling;
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

function eraseSearch(){
	document.getElementById("search").value ="";
}