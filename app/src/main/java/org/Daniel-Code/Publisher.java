public final class Publisher {
	final int num;

	private Publisher(int number) {
		// Initialization
		this.num = number;
	}

	public static Publisher newInstance(int number) {
		Publisher published = new Publisher(number);
		return published;
	}
}


