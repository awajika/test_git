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

    if (!nowSort == false || nowSort == "asc") {
        idSort.setAttribute("value", "desc");
        idSortBtn.setAttribute("form", "userSearchForm")
    }

    if (nowSort == "desc") {
        idSort.setAttribute("value", "asc");
        idSortBtn.setAttribute("form", "userSearchForm")
    }
}

// 氏名ソートボタン押下
 function nameSortButton(name, nowSort) {
     const nameSort = document.getElementById("nameSort");
     const nameSortBtn = document.getElementById("nameSortBtn");

     if (!nowSort == false || nowSort == "asc") {
         nameSort.setAttribute("value", "desc");
         nameSortBtn.setAttribute("form", "userSearchForm")
     }

     if (nowSort == "desc") {
         nameSort.setAttribute("value", "asc");
         nameSortBtn.setAttribute("form", "userSearchForm")
     }
 }

