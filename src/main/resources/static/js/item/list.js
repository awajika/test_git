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