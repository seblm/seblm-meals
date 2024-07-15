<div class="spinner-container">
    <div class="spinner"></div>
</div>

<style lang="scss">
    .spinner-container {
      position: fixed;
      top: 0;
      height: 100vh;
      width: 100vw;
      background: var(--color-bg-2);
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .spinner {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      position: relative;
      animation: rotate 1s linear infinite
    }
    .spinner::before , .spinner::after {
      content: "";
      box-sizing: border-box;
      position: absolute;
      inset: 0;
      border-radius: 50%;
      border: 5px solid #FFF;
      animation: prixClipFix 2s linear infinite ;
    }
    .spinner::after{
      border-color: var(--color-theme-2);
      animation: prixClipFix 2s linear infinite , rotate 0.5s linear infinite reverse;
      inset: 6px;
    }

    @keyframes rotate {
      0%   {transform: rotate(0deg)}
      100%   {transform: rotate(360deg)}
    }

    @keyframes prixClipFix {
      0%   {clip-path:polygon(50% 50%,0 0,0 0,0 0,0 0,0 0)}
      25%  {clip-path:polygon(50% 50%,0 0,100% 0,100% 0,100% 0,100% 0)}
      50%  {clip-path:polygon(50% 50%,0 0,100% 0,100% 100%,100% 100%,100% 100%)}
      75%  {clip-path:polygon(50% 50%,0 0,100% 0,100% 100%,0 100%,0 100%)}
      100% {clip-path:polygon(50% 50%,0 0,100% 0,100% 100%,0 100%,0 0)}
    }
</style>