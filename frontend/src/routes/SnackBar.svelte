<script lang="ts">
	import { fly } from 'svelte/transition';
	export let message: string;
	export let status: 'success' | 'error';
</script>

<div
	class="snackbar"
	class:snackbar-success={status === 'success'}
	class:snackbar-error={status === 'error'}
	transition:fly
>
	{message}
</div>

<style lang="scss">
	.snackbar {
		position: fixed;
		bottom: 1rem;
		left: 50%;
		transform: translate(-50%, 0);
		padding: 1rem 2rem;
		border-radius: 5px;
		background: var(--color-theme-1);
		color: #fff;
		box-shadow: 0 2px 3px rgba(0, 0, 0, 0.2), 0 3px 6px rgba(0, 0, 0, 0.1);
		&-success {
			background: #95d061;
		}
		&-error {
			background: #cc505e;
		}
	}
</style>
