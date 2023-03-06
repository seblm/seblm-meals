<script lang="ts">
	import { arrowLeft } from '$lib/images/arrow-left.js';
	import { arrowRight } from '$lib/images/arrow-right.js';
	import { createEventDispatcher } from 'svelte';

	export let title: string;

	const dispatch = createEventDispatcher();

	function onNext() {
		dispatch('next');
	}

	function onPrev() {
		dispatch('previous');
	}
</script>

<header class="calendar-header">
	<button class="calendar-header-action" on:click={onPrev}>{@html arrowLeft}</button>
	<h2 class="calendar-header-title">{title}</h2>
	<button class="calendar-header-action" on:click={onNext}>{@html arrowRight}</button>
</header>

<style lang="scss">
	.calendar-header {
		display: flex;
		justify-content: space-between;
		&-action {
			color: var(--color-theme-1);
			cursor: pointer;
			border: none;
			border-radius: 50px;
			height: 50px;
			width: 50px;
			display: inline-flex;
			justify-content: center;
			align-items: center;
			transition: 0.2s ease-in;
			&:hover,
			&:focus {
				filter: brightness(0.95);
			}
		}
	}
</style>
