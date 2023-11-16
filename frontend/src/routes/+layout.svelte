<script>
	import './styles.css';
	import Note from '$lib/component/Note.svelte'
</script>

<div class="app">
	<main>
		<slot />
	</main>
	<Note></Note>
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
</style>
