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

<header class="calendar-nav">
	<button class="calendar-nav-action" on:click={onPrev}>{@html arrowLeft}</button>
	<h2 class="calendar-nav-title">{title}</h2>
	<button class="calendar-nav-action" on:click={onNext}>{@html arrowRight}</button>
</header>

<style lang="scss">
	@import '../lib/styles/variables';

	.calendar-nav {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 1rem;
		margin-bottom: $spacing-m;
		&-title {
			text-align: center;
		}
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

	@media (min-width: $screen-m) {
		.calendar-nav {
			margin-bottom: 0;
		}
	}
</style>
