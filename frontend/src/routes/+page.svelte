<script lang="ts">
	import Calendar from './Calendar.svelte';
</script>

<div class="app">
	<main>
		<section>
			<Calendar />
		</section>
	</main>
</div>

<style>
	@import '@fontsource/fira-mono';

	:global {
		:root {
			--font-body:
				Arial, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell,
				'Open Sans', 'Helvetica Neue', sans-serif;
			--font-mono: 'Fira Mono', monospace;
			--color-bg-0: rgb(202, 216, 228);
			--color-bg-1: hsl(209, 36%, 86%);
			--color-bg-2: hsl(224, 44%, 95%);
			--color-theme-1: #638bbd;
			--color-text: #2f466c;
			--column-width: 42rem;
			--column-margin-top: 4rem;
			font-family: var(--font-body);
			color: var(--color-text);
		}

		:focus-visible {
			outline: var(--color-bg-0) auto 1px;
		}

		body {
			font-size: 14px;
			min-height: 100vh;
			margin: 0;
			background-attachment: fixed;
			background-size: 100vw 100vh;
		}

		h1,
		h2,
		p {
			font-weight: 400;
		}

		p {
			line-height: 1.5;
		}

		a {
			color: var(--color-theme-1);
			text-decoration: none;
		}

		a:hover {
			text-decoration: underline;
		}

		h1 {
			font-size: 1.8rem;
			text-align: center;
		}

		h2 {
			font-size: 1.2rem;
		}

		input,
		button {
			font-size: inherit;
			font-family: inherit;
		}

		button:focus:not(:focus-visible) {
			outline: none;
		}

		@media (min-width: 769px) {
			body {
				font-size: 16px;
			}

			h1 {
				font-size: 2.4rem;
			}

			h2 {
				font-size: 1.5rem;
			}
		}

		.spacer {
			flex: 1 1 auto;
		}
	}

	.app {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}

	main {
		flex: 1;
		display: flex;
		flex-direction: column;
		padding: 1rem;
		width: 100%;
		max-width: 1400px;
		margin: 0 auto;
		box-sizing: border-box;
	}
	section {
		display: flex;
		flex-direction: column;
		justify-content: flex-start;
		align-items: center;
		flex: 0.6;
	}
</style>
