let commonChoiceNo = document.getElementById('common-choice-no');
commonChoiceNo.addEventListener('click', function() {
  // 共通選択POPUP-NO
  $('#common-choice').modal('hide');
});

let commonNgClose = document.getElementById('common-ng-close');
// 共通エラーPOPUP-閉じる
commonNgClose.addEventListener('click', function () {
  $('#common-ng').modal('hide');
});

// ローディング表示
function showLoading() {
    const modal = document.getElementById('loading-modal');
    const overlay = document.getElementById('overlay');
    modal.style.display = 'block';
    overlay.style.display = 'block';
}

// ローディング表示削除
function removeLoading() {
    const modal = document.getElementById('loading-modal');
    const overlay = document.getElementById('overlay');
    modal.style.display = 'none';
    overlay.style.display = 'none';
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
};