/**
 * Small program that uses the compliant Publisher static factory method.
 */
public final class PublisherDemo {
	private PublisherDemo() {
		// no-op
	}

	public static void main(String[] args) {
		int number = 0;
		if (args != null && args.length > 0) {
			try {
				number = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				System.out.println("Usage: java PublisherDemo <integer>");
				return;
			}
		}

		Publisher published = Publisher.newInstance(number);
		System.out.println("Published num=" + published.num);
	}
}
