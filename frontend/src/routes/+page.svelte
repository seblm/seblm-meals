<script>
	import Calendar from './Calendar.svelte';
</script>

<svelte:head>
	<title>Seb LM Meals</title>
	<meta name="description" content="A yummy app for a relaxed meals planning" />
</svelte:head>

<section>
	<Calendar />
</section>

<style>
	section {
		display: flex;
		flex-direction: column;
		justify-content: flex-start;
		align-items: center;
		flex: 0.6;
	}
</style>
