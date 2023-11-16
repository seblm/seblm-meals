<script lang="ts">
    let open = false;
</script>

<div class="note">
    <div class="note-dialog" class:open={open}>
        tagada tsoin tsoin
    </div>
    <button class="note-fab" on:click={() => open = !open}>Note</button>
</div>

<style lang="scss">
    .note {
      position: fixed;
      top: 20px;
      right: 20px;
      &-fab,
      &-dialog {
        position: absolute;
        top: 0;
        right: 0;

      }
      &-dialog {
        max-width: 0;
        max-height: 0;
        width: 90vw;
        height: 90vh;
        background: #ffffff;
        transition: .2s ease-in;
        overflow: hidden;
        &.open {
          max-height: 100vh;
          max-width: 100vw;
        }
      }
    }
</style>
