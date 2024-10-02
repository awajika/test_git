// 編集ボタン
function editItem(element) {
    const id = element.getAttribute('data-id');
    window.location.href = `/item/form/` + id;
}

// 新規登録ボタン
function entryItem(element) {
    window.location.href = `/item/form`;
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

// 全選択ボタン押下
function allCheck(element) {
  let check = $(element).attr('data-check');
  if (check == 'true') {
    // 全選択
    $('[id^=item-]').prop('checked', true);
    $(element).attr('data-check', false);
  } else {
    // 全解除
    $('[id^=item-]').prop('checked', false);
    $(element).attr('data-check', true);
  }
}

let lists;
// 削除ボタン
function deleteItem(element) {
  let itemId = $(element).attr('data-id');
  lists = [];
  lists.push(itemId);

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
function deleteItemList(element) {
  lists = getSelectedItemId();
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
function getSelectedItemId() {
  const selectedList = [];
  const ids = $('[id^=item-id]');
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
});

async function uploadItem() {
  // ローディング表示
  showLoading();

  await sleep(1000);
  removeLoading();
}

async function downloadItem() {
  // ローディング表示
  showLoading();

  await sleep(1000);
  removeLoading();
}

// 商品コードソートボタン押下
function IdSortButton(name, nowSort) {
    const result = checkDate();

    if (result) {
        const idSort = document.getElementById("idSort");
        const idSortBtn = document.getElementById("idSortBtn");

        if (nowSort == "asc") {
            idSort.setAttribute("value", "desc");
            idSortBtn.setAttribute("form", "itemSearchForm");
        }

        if (nowSort == "" || nowSort == "desc") {
            idSort.setAttribute("value", "asc");
            idSortBtn.setAttribute("form", "itemSearchForm");
        }
    }
}

// 単価ソートボタン押下
function PriceSortButton(name, nowSort) {
    const result = checkDate();

    if (result) {
        const priceSort = document.getElementById("priceSort");
        const priceSortBtn = document.getElementById("priceSortBtn");

        if (nowSort == "asc") {
            priceSort.setAttribute("value", "desc");
            priceSortBtn.setAttribute("form", "itemSearchForm");
        }

        if (nowSort == "" || nowSort == "desc") {
            priceSort.setAttribute("value", "asc");
            priceSortBtn.setAttribute("form", "itemSearchForm");
        }
    }
}

// 合計ソートボタン押下
function TotalSortButton(name, nowSort) {
    const result = checkDate();

    if (result) {
        const totalSort = document.getElementById("totalSort");
        const totalSortBtn = document.getElementById("totalSortBtn");

        if (nowSort == "asc") {
            totalSort.setAttribute("value", "desc");
            totalSortBtn.setAttribute("form", "itemSearchForm");
        }

        if (nowSort == "" || nowSort == "desc") {
            totalSort.setAttribute("value", "asc");
            totalSortBtn.setAttribute("form", "itemSearchForm");
        }
    }
}

// 購入日時ソートボタン押下
function CreateSortButton(name, nowSort) {
    const result = checkDate();

    if (result) {
        const createSort = document.getElementById("createSort");
        const createSortBtn = document.getElementById("createSortBtn");

        if (nowSort == "asc") {
            createSort.setAttribute("value", "desc");
            createSortBtn.setAttribute("form", "itemSearchForm");
        }

        if (nowSort == "" || nowSort == "desc") {
            createSort.setAttribute("value", "asc");
            createSortBtn.setAttribute("form", "itemSearchForm");
        }
    }
}

// 購入日時を設定した状態で検索ボタン押下
const btn = document.getElementById("searchBtn");
btn.addEventListener('click', function() {
    const result = checkDate()

    if (result) {
        btn.setAttribute("type", "submit");
    }
});

// 商品コードアップロードボタン押下
async function uploadItem() {

    // ローディング表示
    showLoading();
    await sleep(1000);
    removeLoading();

    uploadFile();
};

// 商品マスタ一括登録処理
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
        url: "/item/upload",
        data: formData,
        cache       : false,
        contentType : false,
        processData : false,
        dataType    : "json"
    }).then(function (response) {
        // リクエストヘッダのmessageにメッセージがあるか確認
        if (response.message != null) {
            let messageFlag = response.message;
            // reload()だと下のページングの数字に不具合が生じるためlocation.hrefを使ってページの再読み込み
            location.href = "/item/list?successMessage=" + messageFlag;
        }

    }, function (response) {

        // 権限のないユーザーが/item/uploadにアクセスしたときの処理
        if (response.responseJSON.message == "forbidden") {
            window.location.href = "http://localhost:8080/item/list";
        }

        // リクエストヘッダのmessageにメッセージがあるか確認
        if (response.responseJSON.message != null) {
            let errorList = response.responseJSON.message;

             // サーバから受け取ったerrorListを取り出し、モーダルに書き込む
             let errorMessage = "";
             $.each(errorList, function(i, error) {
                errorMessage = errorMessage + (error + '\r\n')
             });

             // モーダルにエラーメッセージを追加
             $("#common-ng-message").text(errorMessage);

             // モーダル表示
             $("#common-ng").modal("show");
        }
    });
}

// 商品コードダウンロードボタン押下
async function downloadItem() {

    // ローディング表示
    showLoading();
    await sleep(1000);
    removeLoading();

    downloadFile();
}

// 商品マスタダウンロード処理
function downloadFile() {

    //　SpringSecurityの閲覧禁止を回避するために、csrf情報をセット
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    // Ajax通信時に、リクエストヘッダにトークンを埋め込むよう記述
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });

    $.ajax({
        type: "post",
        url: "/item/download",
        cache       : false,
        contentType : false,
        processData : false,
        dataType    : "html"
    }).then(function(data) {

        // 現在日時を取得
        let currentDate = formatDate();
        // ファイル名　item_yyyyMMddHHmmss.csv
        let fileName = "item_" + currentDate +".csv";

        let blob = new Blob([data]);
        let objURL = window.URL.createObjectURL(blob);

        // 新しくリンクを生成
        let link = document.createElement("a");
        document.body.appendChild(link);
        link.href = objURL;
        link.download = fileName;

        // ファイルダウンロード
        link.click();

    }, function () {
        // モーダルにエラーメッセージを追加
        $("#common-ng-message").text("商品コードダウンロードに失敗しました");

        // モーダル表示
        $("#common-ng").modal("show");
    });
}

// 現在日時を取得し、フォーマットを整える
function formatDate() {

    let currentDate = new Date();

    let year = String(currentDate.getFullYear());

    let mouth = String(currentDate.getMonth() + 1);
    if (mouth.length == 1) {
        mouth = "0" + mouth;
    }

    let date = String(currentDate.getDate());
    if (date.length == 1) {
        date = "0" + date;
    }

    let hours = String(currentDate.getHours());
    if (hours.length == 1) {
        hours = "0" + hours;
    }

    let minutes = String(currentDate.getMinutes());
    if (minutes.length == 1) {
        minutes = "0" + minutes;
    }

    let seconds = String(currentDate.getSeconds());
    if (seconds.length == 1) {
        seconds = "0" + seconds;
    }

    let formatDate = year + mouth + date + hours + minutes + seconds;
    return formatDate;
}

// 日付チェック
// 入力された日付が正常な場合はtrue、エラーの場合はfalseを返す
function checkDate() {
    const startAtValue = document.getElementById("startAt").value;
    const endAtValue = document.getElementById("endAt").value;

    // 日付が入力されていたときチェック
    if (startAtValue != "" && endAtValue != "" && endAtValue < startAtValue) {
        // モーダルにエラーメッセージを追加
        $("#common-ng-message").text("開始日は終了日よりも前の日付を設定してください");

        // モーダル表示
        $("#common-ng").modal("show");
        return false;
    } else {
        return true;
    }
}