# xplane-map

https://github.com/luizcantoni/x-pi

Check This:
public class XPlaneDataPacketDecoder implements XPlaneDataPacketObserver {

	private DATAMessageRepository dataMessageRepository;
	private DATAGroupRepository dataGroupRepository;

	BlockingQueue<XPlaneUDPMessage> messageQueue;

	public XPlaneDataPacketDecoder(DATAMessageRepository dataMessageRepository, DATAGroupRepository dataGroupRepository) {
		this.dataMessageRepository = dataMessageRepository;
		this.dataGroupRepository = dataGroupRepository;
	}

	public void newSimData(byte[] sim_data) throws Exception {
		// identify the packet type (identified by the first four bytes)
		String packet_type = new String(sim_data,0,4).trim();

		if (packet_type.equals("DATA")) {
			int index;//, segments;
			float value = -999;
			//segments = (sim_data.length - 5)/36;

			ByteBuffer byteBuffer = ByteBuffer.wrap(sim_data, 5, sim_data.length - 5);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			for (int seg = 0; seg < dataMessageRepository.getAllowedMessages().size(); seg++) {
				index = byteBuffer.getInt();

				if (dataMessageRepository.getAllowedMessages().contains(index)) {
					DATAMessage dataMessage = dataMessageRepository.getMessage(index);
					DATA data = null;

					if (dataMessage != null) {
						for (int i = 0; i < 8; i++) {
							value = byteBuffer.getFloat();

							dataMessage.getRXData()[i] = value;

							data = dataGroupRepository.getDATA(index, i);

							if (data != null) {
								data.setValue(value);
							}
						}
					}
				}
			}
		}
	}
}
