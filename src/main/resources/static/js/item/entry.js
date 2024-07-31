// 削除ボタン
let lists;
function deleteItem(element){
  let itemId = $(element).attr('data-id');
  lists = [];
  lists.push(itemId);

  // 確認モーダル表示
  $('#common-choice-message').text('削除しますか？');
  $('#common-choice').modal('show');
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