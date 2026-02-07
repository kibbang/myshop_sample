(function () {
  if (window.__confirmSubmitBound) return;
  window.__confirmSubmitBound = true;

  document.addEventListener('submit', function (e) {
    const form = e.target;
    if (!(form instanceof HTMLFormElement)) return;
    if (!form.classList.contains('js-confirm-submit')) return;

    // 중복 제출 방지
    if (form.dataset.submitted === 'true') {
      e.preventDefault();
      return;
    }

    const msg = form.dataset.confirmMessage;
    if (msg && !confirm(msg)) {
      e.preventDefault();
      return;
    }

    // 확인이면 제출 진행 + 버튼 비활성화
    form.dataset.submitted = 'true';
    const btn = form.querySelector('button[type="submit"], input[type="submit"]');
    if (btn) btn.disabled = true;
  }, true);
})();


