<script lang="ts">
	interface Props {
		title: string;
		onPrev: () => void;
		onNext: () => void;
	}
	let { title, onPrev, onNext }: Props = $props();
</script>

<header class="calendar-nav">
	<button
		class="calendar-nav-action fa7-regular--arrow-alt-circle-left"
		onclick={onPrev}
		title="previous"
	></button>
	<h2 class="calendar-nav-title">{title}</h2>
	<button
		class="calendar-nav-action fa7-regular--arrow-alt-circle-right"
		onclick={onNext}
		title="next"
	></button>
</header>

<style>
	@import 'icon-arrow-left.css';
	@import 'icon-arrow-right.css';
	.calendar-nav {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 1rem;
		margin-bottom: 1.6rem;
		.calendar-nav-title {
			text-align: center;
		}
		.calendar-nav-action {
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

	@media (min-width: 769px) {
		.calendar-nav {
			margin-bottom: 0;
		}
	}
</style>
