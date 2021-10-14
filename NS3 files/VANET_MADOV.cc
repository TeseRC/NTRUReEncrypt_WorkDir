#include "ns3/core-module.h"
#include "ns3/network-module.h"
#include "ns3/mobility-module.h"
#include "ns3/config-store-module.h"
#include "ns3/wifi-module.h"
#include "ns3/internet-module.h"
#include "ns3/wave-net-device.h"
#include "ns3/wave-mac-helper.h"
#include "ns3/wave-helper.h"
#include "ns3/ipv4-static-routing-helper.h"
#include "ns3/ipv4-list-routing-helper.h"
#include "ns3/yans-wifi-phy.h"
#include "ns3/netanim-module.h"
#include <vector>
#include "ns3/flow-monitor-module.h"
#include <cassert>
#include "ns3/gnuplot.h"
#include "ns3/maodv-module.h"
#include "ns3/mac-gplot.h"
#include "ns3/ipv6-static-routing-helper.h"
#include "ns3/ipv6-routing-table-entry.h"
#include <iostream>
#include <fstream>
#include <string>
#include<iostream>
#include<math.h>
#include<string.h>
#include<stdlib.h>
using namespace ns3;
using namespace std;
NS_LOG_COMPONENT_DEFINE ("VANET_MAODV");
AnimationInterface *pAnim;
double ds=1000.0;
uint32_t packetSize = 1024; 
uint32_t numPackets = 10;       
double interval = 1.0; 
Time interPacketInterval = Seconds (interval);
void compare_Minimum(double dis){
if(ds>dis){ds=dis;}}
void getNearbynodesrc(NodeContainer vechdev){
	int nn=1;
	double x1=250;
	double y1=250;
	for(uint32_t i=0;i<vechdev.GetN ();i++){
		Ptr<RandomWaypointMobilityModel> FCMob = vechdev.Get(i)->GetObject<RandomWaypointMobilityModel>();
		Vector m_position = FCMob->GetPosition();
		double x=m_position.x;
		double y=m_position.y;
		double xx=x1-x;
		double yy=y1-y;
		double x2=(xx*xx);
		double y2=(yy*yy);
		double sx=sqrt(x2);
		double sy=sqrt(y2);
		double dis=(sx+sy);
		compare_Minimum(dis);
		if(ds<=100){
			if(nn==1){
				pAnim->UpdateNodeColor (vechdev.Get (i), 255,0, 250); nn=2;
			}
		}
	}
}

void ReceivePacket (Ptr<Socket> socket){
	while (socket->Recv ()){
		NS_LOG_UNCOND ("Received one packet!");
	}
}

static void GenerateTraffic (Ptr<Socket> socket, uint32_t pktSize,uint32_t pktCount, Time pktInterval ){
	if (pktCount > 0){
		socket->Send (Create<Packet> (pktSize));
		Simulator::Schedule (pktInterval, &GenerateTraffic,socket, pktSize,pktCount-1, pktInterval);
	}
	else{socket->Close ();}
}

void PktTrans1(NodeContainer b,NodeContainer c, NodeContainer d){
	std::cout<<"\n  Select the source[RSU] and destination [Any Vehicle] and perform the RREQ and RREP transmission between the nodes. \n";
	for(  uint32_t i=0;i<b.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (b.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
	for(  uint32_t i=0;i<c.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (c.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
}

void PktTrans3(NodeContainer b,NodeContainer c, NodeContainer d){
	for(  uint32_t i=0;i<b.GetN ();i++){
	TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
	Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
	InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
	recvSink1->Bind (local1);
	recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
	Ptr<Socket> source = Socket::CreateSocket (b.Get (i), tid1);
	InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
	source->SetAllowBroadcast (true);
	source->Connect (remote);
	Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);}
	for(  uint32_t i=0;i<c.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (c.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
}

void PktTrans2(NodeContainer b,NodeContainer c, NodeContainer d){
	std::cout<<"\n  Select the path between the nodes by using MULTICAST AD-HOC ON-DEMAND DISTANCE VECTOR ROUTING PROTOCOL(MAODV Routing Protocol).  \n\n";
	for(  uint32_t i=0;i<b.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (b.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
	for(  uint32_t i=0;i<c.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (c.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
}

void PktTrans4(NodeContainer b,NodeContainer c, NodeContainer d){
	std::cout<<"\n Perform the RSU to vehicle communication between the nodes   \n\n";
	for(  uint32_t i=0;i<b.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (b.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
	for(  uint32_t i=0;i<c.GetN ();i++){
		TypeId tid1 = TypeId::LookupByName ("ns3::UdpSocketFactory");
		Ptr<Socket> recvSink1 = Socket::CreateSocket (d.Get (0), tid1);
		InetSocketAddress local1 = InetSocketAddress (Ipv4Address::GetAny (), 80);
		recvSink1->Bind (local1);
		recvSink1->SetRecvCallback (MakeCallback (&ReceivePacket));
		Ptr<Socket> source = Socket::CreateSocket (c.Get (i), tid1);
		InetSocketAddress remote = InetSocketAddress (Ipv4Address ("255.255.255.255"), 80);
		source->SetAllowBroadcast (true);
		source->Connect (remote);
		Simulator::ScheduleWithContext (source->GetNode ()->GetId (),Seconds (0.1), &GenerateTraffic,source, packetSize, numPackets,interPacketInterval);
	}
}

int main (int argc, char *argv[]){
	std::string phyMode ("DsssRate1Mbps");
	double distance = 600;  
	uint16_t numGroup_A_Nodes = 10; 
	uint16_t numGroup_B_Nodes = 10; 
	uint16_t numRSU = 4; 
	uint32_t revNode = 0;
	uint32_t sourceNode = numGroup_A_Nodes-1;
	int nodeSpeed = 20; 
	int nodePause = 0; 
	bool enableFlowMonitor = false;
	CommandLine cmd;
	double simtime=300.0;
	cmd.AddValue ("phyMode", "Wifi Phy mode", phyMode);
	cmd.AddValue ("distance", "distance (m)", distance);
	cmd.AddValue ("packetSize", "size of application packet sent", packetSize);
	cmd.AddValue ("numPackets", "number of packets generated", numPackets);
	cmd.AddValue ("interval", "interval (seconds) between packets", interval);
	cmd.AddValue ("numGroup_A_Nodes", "number of Group_A nodes", numGroup_A_Nodes);
	cmd.AddValue ("numGroup_B_Nodes", "number of Group_B nodes", numGroup_B_Nodes);
	cmd.AddValue ("revNode", "Receiver node number", revNode);
	cmd.AddValue ("sourceNode", "Sender node number", sourceNode);
	cmd.AddValue ("EnableMonitor", "Enable Flow Monitor", enableFlowMonitor);
	cmd.Parse (argc, argv); 
	NodeContainer Group_B_Vehicles;
	NodeContainer RSUNodes;
	NodeContainer Group_A_Vehicles;
	std::cout<<"\n  a Network, its consists of 4- RSU and "<< numGroup_A_Nodes+numGroup_B_Nodes  <<"- Vehicles based on the random wave point mobility model.  \n";
	Group_B_Vehicles.Create (numGroup_B_Nodes);
	RSUNodes.Create (numRSU);
	Group_A_Vehicles.Create (numGroup_A_Nodes);
	WifiHelper wifi;
	Ptr<Ipv6ExtensionESP > extension;
	Ptr<Ipv6ExtensionAH> extenAH;
	YansWifiPhyHelper wifiPhy =  YansWifiPhyHelper::Default ();  
	wifiPhy.Set ("RxGain", DoubleValue (-30)); 
	wifiPhy.SetPcapDataLinkType (YansWifiPhyHelper::DLT_IEEE802_11_RADIO); 
	YansWifiChannelHelper wifiChannel;
	wifiChannel.SetPropagationDelay ("ns3::ConstantSpeedPropagationDelayModel");
	wifiChannel.AddPropagationLoss ("ns3::FriisPropagationLossModel");
	wifiPhy.SetChannel (wifiChannel.Create ());
	NqosWifiMacHelper wifiMac = NqosWifiMacHelper::Default ();
	wifi.SetStandard (WIFI_PHY_STANDARD_80211b);
	wifi.SetRemoteStationManager ("ns3::ConstantRateWifiManager","DataMode",StringValue (phyMode),"ControlMode",StringValue (phyMode)); 
	wifiMac.SetType ("ns3::AdhocWifiMac");
	NetDeviceContainer B_Vehicles = wifi.Install (wifiPhy, wifiMac, Group_B_Vehicles);
	NetDeviceContainer A_Vehicles = wifi.Install (wifiPhy, wifiMac, Group_A_Vehicles);
	NetDeviceContainer RSUDevices;
	RSUDevices = wifi.Install (wifiPhy, wifiMac, RSUNodes);
	YansWifiChannelHelper waveChannel = YansWifiChannelHelper::Default ();
	YansWavePhyHelper wavePhy =  YansWavePhyHelper::Default ();
	wavePhy.SetChannel (waveChannel.Create ());
	wavePhy.SetPcapDataLinkType (YansWifiPhyHelper::DLT_IEEE802_11);
	QosWaveMacHelper waveMac = QosWaveMacHelper::Default ();
	WaveHelper waveHelper = WaveHelper::Default ();
	B_Vehicles = waveHelper.Install (wavePhy, waveMac, Group_B_Vehicles);
	A_Vehicles = waveHelper.Install (wavePhy, waveMac, Group_A_Vehicles);
	int64_t streamIndex = 0;
	ObjectFactory pos;
	pos.SetTypeId ("ns3::RandomRectanglePositionAllocator");
	pos.Set ("X", StringValue ("ns3::UniformRandomVariable[Min=1.0|Max=1000.0]"));
	pos.Set ("Y", StringValue ("ns3::UniformRandomVariable[Min=1.0|Max=1000.0]"));
	Ptr<PositionAllocator> taPositionAlloc = pos.Create ()->GetObject<PositionAllocator> ();
	streamIndex += taPositionAlloc->AssignStreams (streamIndex);
	MobilityHelper mobility;
	mobility.SetPositionAllocator(taPositionAlloc);
	std::stringstream ssSpeed;
	ssSpeed << "ns3::UniformRandomVariable[Min=0.0|Max=" << nodeSpeed << "]";
	std::stringstream ssPause;
	ssPause << "ns3::ConstantRandomVariable[Constant=" << nodePause << "]";
	mobility.SetMobilityModel ("ns3::RandomWaypointMobilityModel","Speed", StringValue (ssSpeed.str ()),"Pause", StringValue (ssPause.str ()),
	"PositionAllocator", PointerValue (taPositionAlloc));
	mobility.Install (Group_B_Vehicles);
	mobility.Install (Group_A_Vehicles);
	MobilityHelper mobility1;
	Simulator::Schedule (Seconds (0.3), &getNearbynodesrc,Group_B_Vehicles);
	mobility1.SetMobilityModel ("ns3::ConstantPositionMobilityModel");
	mobility1.Install (RSUNodes);
	AnimationInterface::SetConstantPosition (RSUNodes.Get (0), 250, 250);
	AnimationInterface::SetConstantPosition (RSUNodes.Get (1), 750, 250);
	AnimationInterface::SetConstantPosition (RSUNodes.Get (2), 250, 750);
	AnimationInterface::SetConstantPosition (RSUNodes.Get (3), 750, 750);
	Simulator::Schedule (Seconds (0.5), &PktTrans1,Group_A_Vehicles,Group_B_Vehicles, RSUNodes);
	MAodvHelper maodv;
	Ipv4StaticRoutingHelper staticRouting;
	Ipv4ListRoutingHelper list;
	list.Add (staticRouting, 0);
	list.Add (maodv, 1);
	InternetStackHelper internet;
	internet.SetRoutingHelper (list); 
	internet.Install (Group_B_Vehicles);
	internet.Install (RSUNodes);
	internet.Install (Group_A_Vehicles);
	Ipv4AddressHelper ipv4h;
	ipv4h.SetBase ("1.0.0.0", "255.0.0.0");
	InternetStackHelper internetv6;
	internetv6.SetIpv4StackInstall (false);
	Ipv4AddressHelper ipv4;
	NS_LOG_INFO ("Assign IP Addresses.");
	ipv4.SetBase ("10.1.1.0", "255.255.255.0");
	Ipv4InterfaceContainer i = ipv4.Assign (B_Vehicles);
	Ipv4InterfaceContainer ii = ipv4.Assign (A_Vehicles);
	Ipv4InterfaceContainer iii = ipv4.Assign (RSUDevices);
	TypeId tid = TypeId::LookupByName ("ns3::UdpSocketFactory");
	Ptr<Socket> recvSink = Socket::CreateSocket (Group_B_Vehicles.Get (revNode), tid);
	InetSocketAddress local = InetSocketAddress (Ipv4Address::GetAny (), 80);
	Simulator::Schedule (Seconds (2.3), &PktTrans3, Group_A_Vehicles,Group_B_Vehicles,RSUNodes);
	Simulator::Schedule (Seconds (3.3), &PktTrans4, Group_A_Vehicles,Group_B_Vehicles,RSUNodes);
	recvSink->Bind (local);
	recvSink->SetRecvCallback (MakeCallback (&ReceivePacket));
	Ptr<Socket> source = Socket::CreateSocket (Group_B_Vehicles.Get (sourceNode), tid);
	InetSocketAddress remote = InetSocketAddress (i.GetAddress (revNode, 0), 80);
	source->Connect (remote);
	Simulator::Schedule (Seconds (1.3), &GenerateTraffic, source, packetSize, numPackets, interPacketInterval);
	Simulator::Schedule (Seconds (1.5), &PktTrans2, Group_A_Vehicles,Group_B_Vehicles,RSUNodes);
	Simulator::Stop (Seconds (simtime));
	macgplot mg;
	mg.Delay((int)numGroup_A_Nodes+numGroup_B_Nodes,"MAODV");
	mg.Packet_Loss_Ratio((int)numGroup_A_Nodes+numGroup_B_Nodes,"MAODV");
	mg.Throughput((int)numGroup_A_Nodes+numGroup_B_Nodes,"MAODV");
	mg.Packet_Delivery_Ratio((int)numGroup_A_Nodes+numGroup_B_Nodes,"MAODV");
	pAnim= new AnimationInterface ("VANET_MAODV.xml");
	pAnim->SetBackgroundImage ("/home/bifes/ns-3/ns-3/src/netanim/img1/bg.png", -925, -725, 3.5, 8.5, 1.0);
	uint32_t RSUimg =pAnim->AddResource("/home/bifes/ns-3/ns-3/src/netanim/img1/RSU.png");
	uint32_t userimg =pAnim->AddResource("/home/bifes/ns-3/ns-3/src/netanim/img1/A_Vehicle.png");
	uint32_t Deviceimg =pAnim->AddResource("/home/bifes/ns-3/ns-3/src/netanim/img1/B_Vehicle.png");
	for(  uint32_t i=0;i<Group_B_Vehicles.GetN ();i++){
		pAnim->UpdateNodeDescription (Group_B_Vehicles.Get (i), "B_Vehicle"); 
		Ptr<Node> wid= Group_B_Vehicles.Get (i);
		uint32_t nodeId = wid->GetId ();
		pAnim->UpdateNodeImage (nodeId, Deviceimg);
		pAnim->UpdateNodeColor(Group_B_Vehicles.Get(i), 255, 255, 0); 
		pAnim->UpdateNodeSize (nodeId, 60.0,60.0);
	}
	for(  uint32_t i=0;i<RSUNodes.GetN ();i++){
		pAnim->UpdateNodeDescription (RSUNodes.Get (i), "RSU"); 
		Ptr<Node> wid= RSUNodes.Get (i);
		uint32_t nodeId = wid->GetId ();
		pAnim->UpdateNodeImage (nodeId, RSUimg);
		pAnim->UpdateNodeColor(RSUNodes.Get(i), 0, 255, 0); 
		pAnim->UpdateNodeSize (nodeId, 125.0,125.0);
	}
	for( uint32_t i=0;i<Group_A_Vehicles.GetN ();i++){
		pAnim->UpdateNodeDescription (Group_A_Vehicles.Get (i), "A_Vehicle"); 
		Ptr<Node> wid= Group_A_Vehicles.Get (i);
		uint32_t nodeId = wid->GetId ();
		pAnim->UpdateNodeImage (nodeId, userimg);
		pAnim->UpdateNodeColor(Group_A_Vehicles.Get(i), 0, 255, 0); 
		pAnim->UpdateNodeSize (nodeId, 60.0,60.0);
	}
	FlowMonitorHelper flowmon;
	Ptr<FlowMonitor> monitor = flowmon.InstallAll();
	Simulator::Run ();
	monitor->CheckForLostPackets ();
	uint32_t LostPacketsum = 0;
	uint32_t rxPacketsum = 0;
	uint32_t txPacketsum = 0;
	uint32_t DropPacketsum = 0;
	Ptr<Ipv4FlowClassifier> classifier = DynamicCast<Ipv4FlowClassifier> (flowmon.GetClassifier ());
	std::map<FlowId, FlowMonitor::FlowStats> stats = monitor->GetFlowStats ();
	for(std::map<FlowId, FlowMonitor::FlowStats>::const_iterator i = stats.begin (); i != stats.end (); ++i){
		rxPacketsum += (i->second.txBytes/(numGroup_A_Nodes*10));
		LostPacketsum += i->second.lostPackets;
		DropPacketsum += i->second.packetsDropped.size();
		txPacketsum=rxPacketsum+LostPacketsum+DropPacketsum;
	}
	std::cout<<"Sum of Transmitted Packets  "<<txPacketsum <<"\n\n";
	std::cout<<"Sum of Receive Packets  "<<rxPacketsum <<"\n\n";
	std::cout<<"Sum of Lost Packets  "<<LostPacketsum <<"\n\n";
	std::cout<<"Sum of Drop Packets  "<<DropPacketsum <<"\n\n";
	Simulator::Destroy ();
	system("gnuplot 'Delay.plt'");
	system("gnuplot 'Packet_Loss_Ratio.plt'");
	system("gnuplot 'Throughput.plt'");
	system("gnuplot 'Packet_Delivery_Ratio.plt'");
	return 0;
}
