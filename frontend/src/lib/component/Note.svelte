<script lang="ts">
	import { edit } from '$lib/images/edit';

  let open = false;
</script>

<div class="note" class:open>
	<section class="note-dialog" class:open>
		<div class="note-dialog-content">
      <h2 class="note-dialog-title">Notes</h2>
      <div class="note-dialog-body"></div>
    </div>
	</section>
	<button class="note-fab" on:click={() => (open = !open)}>{@html edit}</button>
</div>

<style lang="scss">
	$note-margin: 20px;
	.note {
		position: fixed;
		top: $note-margin;
		right: $note-margin;
    transition: 0.2s ease-in;
    max-height: 0;
    max-width: 0;
    height: calc(100vh - $note-margin * 2);
    width: calc(100vw - $note-margin * 2);
    &.open {
      max-height: calc(100vh - $note-margin * 2);
      max-width: calc(100vw - $note-margin * 2);
    }
		&-fab,
		&-dialog {
			position: absolute;
			top: 0;
			right: 0;
      z-index: 999;
      border-radius: 20px;
		}
    &-fab {
      background: #fff;
      border: none;
      height: 50px;
      width: 50px;
      color: var(--color-theme-1);
      padding: 10px;
      cursor: pointer;
    }
		&-dialog {
			max-width: 0;
			max-height: 0;
			width: 100%;
			height: 100%;
			background: #ffffff;
			transition: 0.2s ease-in;
			overflow: hidden;
      opacity: 0;
			&.open {
				max-height: 100%;
				max-width: 100%;
        opacity: 1;
        transition: 0.2s ease-in;

				.note-dialog {
					&-content {
            padding: 20px 40px;
					}
				}
			}
		}
	}
</style>
