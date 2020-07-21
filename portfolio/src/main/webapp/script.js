// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

class ArticleCard {
  constructor(title, text, photoURL, url) {
    this.title = title;
    this.text = text;
    this.photoURL = photoURL;
    this.url = url;
  }

  get html() {
    let card = document.createElement("div");

    card.className = "article-card";
    card.innerHTML = `
      <img src="${this.photoURL}">
      <div class="content">
        <h1>${this.title}</h1>
        <p>${this.text}</p>
      </div>
    `;
    card.onclick = () => { document.location = this.url; };

    let cardWrap = document.createElement("div");
    cardWrap.className = "card-wrap";

    cardWrap.appendChild(card);

    return cardWrap;
  };
}

window.onload = () => {
 
  // TODO: Add getting articles from backend
  let cards = [
    new ArticleCard(
      "Genetic algorithm for distribuition of delivery routes",
      "This was my project in several paper defense competitions.",
      "static/MDVRP.png",
      "static/Genetic algorithm.pdf",
    ),
    new ArticleCard(
      `"Guki" game`,
      "This is a PC game with mechanics similar to Magicka that I was developing (though unfortunately didn't finish) some time ago.",
      "static/Guki.png",
      "https://github.com/Omn1/MagicGame",
    ),
    new ArticleCard(
      "Electronic circuit editor",
      "This is a project that I (mostly) developed for a national competition.",
      "static/Electronic-circuit-editor.png",
      "https://github.com/Omn1/Electronic-circuit-editor",
    ),
    new ArticleCard(
      "Working with input fields on iOS",
      "This is an article I wrote based on my experience working on an iOS app.",
      "static/Input fields.png",
      "https://cruxlab.com/blog/working-with-input-fields-on-ios/",
    )
  ];

  cards.forEach(card => document.getElementById("articles-container").appendChild(card.html));
}
