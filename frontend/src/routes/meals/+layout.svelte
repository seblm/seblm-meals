<script lang="ts">
	import favicon from '$lib/assets/favicon.png';

	let { children } = $props();
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<title>Seb LM Meals — All Meals</title>
	<meta name="description" content="A yummy app for a relaxed meals planning" />
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2.1.1/css/pico.min.css" />
</svelte:head>

{@render children?.()}
