<script lang="ts">
	import favicon from '$lib/assets/favicon.png';

	let { children } = $props();
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<title>Seb LM Meals</title>
	<meta name="description" content="A yummy app for a relaxed meals planning" />
</svelte:head>

{@render children?.()}
