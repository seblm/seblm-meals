<script lang="ts">
	import './styles.css';
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
