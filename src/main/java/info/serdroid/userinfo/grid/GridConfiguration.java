package info.serdroid.userinfo.grid;

public class GridConfiguration {
	
	private int partitionCount;
	private int nodeCount;

	private GridConfiguration() {}
	
	private GridConfiguration(int partitionCount, int nodeCount) {
		this.partitionCount = partitionCount;
		this.nodeCount = nodeCount;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public int getPartitionCount() {
		return partitionCount;
	}

	public static class Builder {
		private GridConfiguration gridConfiguration;
		private static final int defaultPartitionCount = 8;
		private static final int defaultNodeCount = 2;
		public Builder() {
			gridConfiguration = new GridConfiguration(defaultPartitionCount, defaultNodeCount);
		}
		
		public Builder setPartitionCount(int partitionCount) {
			gridConfiguration.partitionCount = partitionCount;
			return this;
		}

		public Builder setNodeCount(int nodeCount) {
			gridConfiguration.nodeCount = nodeCount;
			return this;
		}

		public GridConfiguration build() {
			return gridConfiguration;
		}
	}
}
