// 編集ボタン
function editPerson(element) {
  window.location.href = `edit.html`;
}

// 新規登録ボタン
function signupPerson(element) {
  window.location.href = `signup.html`;
}

let lists;
// 削除ボタン
function deletePerson(element) {
  let personId = $(element).attr('data-id');
  lists = [];
  lists.push(personId);

  if (lists.length != 0) {
    // 確認モーダル表示
    $('#common-choice-message').text('削除しますか？');
    $('#common-choice').modal('show');
  } else {
    // 削除要素がない時、NGモーダル表示
    $('#common-ng-message').text('削除対象が選択されていません。');
    $('#common-ng').modal('show');
  }
}

// 一括削除ボタン
function deletePersonList(element) {
  lists = getSelectedPersonId();

  if (lists.length != 0) {
    // 確認モーダル表示
    $('#common-choice-message').text('選択した要素を削除しますか？');
    $('#common-choice').modal('show');
  } else {
    // 削除要素が選択されていない時、NGモーダル表示
    $('#common-ng-message').text('削除対象が選択されていません。');
    $('#common-ng').modal('show');
  }
}

// 選択中のID取得
function getSelectedPersonId() {
  const selectedList = [];
  const ids = $('[id^=person-id]');
  for (let i = 0; i < ids.length ; i++) {
    if (ids[i].checked) {
      selectedList.push(ids[i].id.split('-')[2]);
    }
  }
  return selectedList;
}

let yesButton = document.getElementById('common-choice-yes');
// 削除処理
yesButton.addEventListener("click", async function() {
  // 確認モーダル非表示
  $('#common-choice').modal('hide');
  // ローディング表示
  showLoading();
  await sleep(1000);
  removeLoading();
  // 一括削除処理
  deleteUsers();
});

// 全選択ボタン押下
function allCheck(element) {
  let check = $(element).attr('data-check');
  if (check == 'true') {
    // 全選択
    $('[id^=person-]').prop('checked', true);
    $(element).attr('data-check', false);
  } else {
    // 全解除
    $('[id^=person-]').prop('checked', false);
    $(element).attr('data-check', true);
  }
}

// toastクラスがついている要素にトーストを適用
window.addEventListener("load", function() {
    let toastElemList = [].slice.call(document.querySelectorAll('.toast'));
    let toastList = toastElemList.map(function (toastElem){
        let toast = new bootstrap.Toast(toastElem, {autohide: false});
        toast.show();
        return toast;
    });
});

// 社員番号ソートボタン押下
function IdSortButton(name, nowSort) {
    const idSort = document.getElementById("idSort");
    const idSortBtn = document.getElementById("idSortBtn");

    if (nowSort == "asc") {
        idSort.setAttribute("value", "desc");
        idSortBtn.setAttribute("form", "userSearchForm")
    }

    if (nowSort == "" || nowSort == "desc") {
        idSort.setAttribute("value", "asc");
        idSortBtn.setAttribute("form", "userSearchForm")
    }
}

// 氏名ソートボタン押下
function nameSortButton(name, nowSort) {
    const nameSort = document.getElementById("nameSort");
    const nameSortBtn = document.getElementById("nameSortBtn");

    if (nowSort == "asc") {
        nameSort.setAttribute("value", "desc");
        nameSortBtn.setAttribute("form", "userSearchForm");
    }

    if (nowSort == "" || nowSort == "desc") {
        nameSort.setAttribute("value", "asc");
        nameSortBtn.setAttribute("form", "userSearchForm");
    }
}

// CSV一括更新ボタンを押下
const csvButton = document.getElementById("csv-upload-btn");
csvButton.addEventListener("click", async function() {

    // ローディング表示
    showLoading();
    await sleep(1000);
    removeLoading();

    uploadFile();
});

// 一括更新処理
function uploadFile() {

    // ファイルを取得
    let file = $("#file")[0].files[0];

    // フォームデータを取得
    let formData = new FormData();
    formData.append("file", file);

    //　SpringSecurityの閲覧禁止を回避するために、csrf情報をセット
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    // Ajax通信時に、リクエストヘッダにトークンを埋め込むよう記述
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.ajax({
      type: "post",
      url: "/person/update",
      data: formData,
      cache       : false,
      contentType : false,
      processData : false,
      dataType    : "json"
    }).then(function (response) {

        // リクエストヘッダのmessageに成功メッセージが存在するか確認
        if (response.message != null) {
            let messageFlag = response.message;
            // reload()だと下のページングの数字に不具合が生じるためlocation.hrefを使ってページの再読み込み
            location.href = "/person/list?successMessage=" + messageFlag;
        }
    }, function (response) {

        // リクエストヘッダのmessageにエラーメッセージが存在するか確認
        if (response.responseJSON.message != null) {
            let errorList = response.responseJSON.message;

            // サーバから受け取ったerrorListを取り出し、モーダルに書き込む
            let errorMessages = "";
            $.each(errorList, function(i, error) {
                errorMessages = errorMessages + (error + '\r\n');
            })

            // モーダルにエラーメッセージを追加
            $('#common-ng-message').text(errorMessages);

            // モーダル表示
            $('#common-ng').modal('show');
        }
    });
}

// 一括削除処理
function deleteUsers() {

    // 選択されたユーザーのuserIdが入ったlist
    lists = getSelectedPersonId();

    // フォームデータを取得
    let formData = new FormData();
    formData.append("lists", lists);

    //　SpringSecurityの閲覧禁止を回避するために、csrf情報をセット
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    // Ajax通信時に、リクエストヘッダにトークンを埋め込むよう記述
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.ajax({
      type: "post",
      url: "/person/delete",
      data: formData,
      cache       : false,
      contentType : false,
      processData : false,
      dataType    : "text"
    }).then(function (response) {
        // reload()だと下のページングの数字に不具合が生じるためlocation.hrefを使ってページの再読み込み
        location.href = "/person/list?successMessage=delete";
    }, function () {
        const row = $(".toast-body").children("span");
        row.text("削除に失敗しました");
    });
}


