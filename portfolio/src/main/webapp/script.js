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

function loadArticleCards() {

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

  cards.forEach(card => $("#articles-container")[0].appendChild(card.html));
}

class BlogEntry {
  constructor(title, content) {
    this.title = title;
    this.content = content;
  }

  get html() {
    let blogEntry = document.createElement("div");
    blogEntry.className = "blog-entry";

    blogEntry.innerHTML = `
      <h2 class="blog-entry-header">${this.title}</h2>
      <div class="blog-entry-content">${this.content}</div>
    `;

    return blogEntry;
  }
}

function loadBlogEntries() {

  // TODO: Add getting blog entries from backend
  let blogEntries = [
    new BlogEntry(
      "My cover band",
      `
      <div style="display: flex; justify-content: space-around; flex-wrap: wrap;">
        <p style="max-width: 400px">
          I've established a cover band with my friends recently.
          We all love rock music and make covers of such bands as
          Linkin Park, System of a Down, Black Sabbath, Nightwish.
          <br><br>
          Most of us don't have musical education, but we're trying our best.
          <br><br>
          I sometimes post our covers to my Instagram, so follow me if you are interested in hearing them.
        </p>
        <img src="static/band.jpg" class="drops-shadow" style="width: 50%; height: 50%;">
      </div>
      `,
    ),
    new BlogEntry(
      "My family member",
      `
        <div style="display: flex; justify-content: flex-begin; flex-wrap: wrap-reverse;">
          <img src="static/Rico1.jpg" class="drops-shadow" style="width: 20%; height: 20%;">
          <img src="static/Rico2.jpg" class="drops-shadow" style="width: 20%; height: 20%; margin-left: 20px;">
          <p style="max-width: 400px; margin-left: 20px">
            This is Rico, a Jack Russell Terrier.
            <br><br>
            He's a very energetic dog, loves playing and
            having long walks in the park.
            <br><br>
            As a terrier, Rico is an earthdog, he is good at hunting and brought several medals from various competitions.
          </p>
        </div>
      `,
    ),
  ];

  blogEntries.forEach(entry => $("#blog-container")[0].appendChild(entry.html));
}

function jsonFromForm(form){
  let formArray = $(form).serializeArray();
  let json = {};

  formArray.forEach((field, _) => json[field.name] = field.value);

  return json;
}

function submitComment(form) {
  let commentJSON = jsonFromForm(form);

  $("#comment-input").val("");

  fetch("/comments", {
    method: "POST",
    body: JSON.stringify(commentJSON)
  })
    .then(response => response.text())
    .then(deleteKey => {
      refreshComments();
      alert(`Delete key for this comment: ${deleteKey}`);
    });

  return false;
}

class Comment {
  constructor(id, author, text) {
    this.id = id
    this.author = author;
    this.text = text;
  }

  get html() {
    let comment = new BlogEntry(`${this.author} says:`, this.text).html;

    let deleteButton = document.createElement("button");
    deleteButton.innerText = "Delete";
    deleteButton.className = "comment-delete";
    deleteButton.innerHTML = `<i class="fa fa-close"></i>`;

    deleteButton.onclick = () => deleteComment(this.id);

    comment.insertBefore(deleteButton, comment.firstChild);

    return comment;
  }
}

class CommentLoader {
  constructor() {
    this.currentPageLink = `/comments?pageSize=${$("#comments-per-page").val()}`;
  }

  loadCurrentPage() {
    fetch(this.currentPageLink)
      .then(response => {
        this.extractPageLinks(response);
        return response.json();
      })
      .then(comments => {
        let commentList = $("#comment-list")[0];

        commentList.innerHTML = "";

        comments.forEach(commentJSON => {
          let comment = new Comment(commentJSON.id, commentJSON.author, commentJSON.text);
          commentList.appendChild(comment.html);
        });
      });
  }

  loadFirstPage() {
    this.currentPageLink = this.firstPageLink;
    this.loadCurrentPage();
  }

  loadPrevPage() {
    this.currentPageLink = this.prevPageLink;
    this.loadCurrentPage();
  }

  loadNextPage() {
    this.currentPageLink = this.nextPageLink;
    this.loadCurrentPage();
  }

  loadLastPage() {
    this.currentPageLink = this.lastPageLink;
    this.loadCurrentPage();
  }

  extractPageLinks(response) {
    let links = response.headers.get("Link")
      .split(", ")
      .map(link => link.match('(.*); rel="(.*)"').slice(1));

    let prevPageLink = links.find(x => x[1] == "prev");
    if (prevPageLink) {
      this.prevPageLink = prevPageLink[0];
      $("#prev-page-button").attr("disabled", false);
    } else {
      $("#prev-page-button").attr("disabled", true);
    }

    let nextPageLink = links.find(x => x[1] == "next");
    if (nextPageLink) {
      this.nextPageLink = nextPageLink[0];
      $("#next-page-button").attr("disabled", false);
    } else {
      $("#next-page-button").attr("disabled", true);
    }

    this.firstPageLink = links.find(x => x[1] == "first")[0];
    this.lastPageLink = links.find(x => x[1] == "last")[0];
  }
}

let commentLoader = new CommentLoader();

function refreshComments() {
  commentLoader = new CommentLoader();
  commentLoader.loadCurrentPage();
}

function deleteComment(id) {

  fetch(`/comments?id=${id}`, {
    method: "DELETE"
  })
    .then(response => {
      if (response.ok) {
        refreshComments();
      } else {
        let deleteKey = prompt("Enter delete key for this comment:");
        fetch(`/comments?id=${id}&deleteKey=${deleteKey}`, {
          method: "DELETE"
        })
          .then(refreshComments);
      }
    });
}
