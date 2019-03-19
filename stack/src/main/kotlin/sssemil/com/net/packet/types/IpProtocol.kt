/*
 * Copyright 2019 Emil Suleymanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sssemil.com.net.packet.types

/**
 * IP-Protocol field representation
 *
 * @author Yotam Harchol (yotam.harchol@bigswitch.com)
 */
class IpProtocol private constructor(val ipProtocolNumber: Short) {

    override fun toString(): String {
        return "0x" + Integer.toHexString(ipProtocolNumber.toInt())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IpProtocol

        if (ipProtocolNumber != other.ipProtocolNumber) return false

        return true
    }

    override fun hashCode(): Int {
        return ipProtocolNumber.toInt()
    }

    companion object {

        val FULL_MASK = IpProtocol(0x0000.toShort())
        internal val MAX_PROTO: Short = 0xFF
        internal val LENGTH = 1
        internal val NUM_HOPOPT: Short = 0x00
        val HOPOPT =
            IpProtocol(IpProtocol.Companion.NUM_HOPOPT)
        val NONE = IpProtocol.Companion.HOPOPT
        val NO_MASK = IpProtocol.Companion.HOPOPT
        internal val NUM_ICMP: Short = 0x01
        val ICMP = IpProtocol(IpProtocol.Companion.NUM_ICMP)
        internal val NUM_IGMP: Short = 0x02
        val IGMP = IpProtocol(IpProtocol.Companion.NUM_IGMP)
        internal val NUM_GGP: Short = 0x03
        val GGP = IpProtocol(IpProtocol.Companion.NUM_GGP)
        internal val NUM_IPv4: Short = 0x04
        val IPv4 = IpProtocol(IpProtocol.Companion.NUM_IPv4)
        internal val NUM_ST: Short = 0x05
        val ST = IpProtocol(IpProtocol.Companion.NUM_ST)
        internal val NUM_TCP: Short = 0x06
        val TCP = IpProtocol(IpProtocol.Companion.NUM_TCP)
        internal val NUM_CBT: Short = 0x07
        val CBT = IpProtocol(IpProtocol.Companion.NUM_CBT)
        internal val NUM_EGP: Short = 0x08
        val EGP = IpProtocol(IpProtocol.Companion.NUM_EGP)
        internal val NUM_IGP: Short = 0x09
        val IGP = IpProtocol(IpProtocol.Companion.NUM_IGP)
        internal val NUM_BBN_RCC_MON: Short = 0x0A
        val BBN_RCC_MON =
            IpProtocol(IpProtocol.Companion.NUM_BBN_RCC_MON)
        internal val NUM_NVP_II: Short = 0x0B
        val NVP_II =
            IpProtocol(IpProtocol.Companion.NUM_NVP_II)
        internal val NUM_PUP: Short = 0x0C
        val PUP = IpProtocol(IpProtocol.Companion.NUM_PUP)
        internal val NUM_ARGUS: Short = 0x0D
        val ARGUS = IpProtocol(IpProtocol.Companion.NUM_ARGUS)
        internal val NUM_EMCON: Short = 0x0E
        val EMCON = IpProtocol(IpProtocol.Companion.NUM_EMCON)
        internal val NUM_XNET: Short = 0x0F
        val XNET = IpProtocol(IpProtocol.Companion.NUM_XNET)
        internal val NUM_CHAOS: Short = 0x10
        val CHAOS = IpProtocol(IpProtocol.Companion.NUM_CHAOS)
        internal val NUM_UDP: Short = 0x11
        val UDP = IpProtocol(IpProtocol.Companion.NUM_UDP)
        internal val NUM_MUX: Short = 0x12
        val MUX = IpProtocol(IpProtocol.Companion.NUM_MUX)
        internal val NUM_DCN_MEAS: Short = 0x13
        val DCN_MEAS =
            IpProtocol(IpProtocol.Companion.NUM_DCN_MEAS)
        internal val NUM_HMP: Short = 0x14
        val HMP = IpProtocol(IpProtocol.Companion.NUM_HMP)
        internal val NUM_PRM: Short = 0x15
        val PRM = IpProtocol(IpProtocol.Companion.NUM_PRM)
        internal val NUM_XNS_IDP: Short = 0x16
        val XNS_IDP =
            IpProtocol(IpProtocol.Companion.NUM_XNS_IDP)
        internal val NUM_TRUNK_1: Short = 0x17
        val TRUNK_1 =
            IpProtocol(IpProtocol.Companion.NUM_TRUNK_1)
        internal val NUM_TRUNK_2: Short = 0x18
        val TRUNK_2 =
            IpProtocol(IpProtocol.Companion.NUM_TRUNK_2)
        internal val NUM_LEAF_1: Short = 0x19
        val LEAF_1 =
            IpProtocol(IpProtocol.Companion.NUM_LEAF_1)
        internal val NUM_LEAF_2: Short = 0x1A
        val LEAF_2 =
            IpProtocol(IpProtocol.Companion.NUM_LEAF_2)
        internal val NUM_RDP: Short = 0x1B
        val RDP = IpProtocol(IpProtocol.Companion.NUM_RDP)
        internal val NUM_IRTP: Short = 0x1C
        val IRTP = IpProtocol(IpProtocol.Companion.NUM_IRTP)
        internal val NUM_ISO_TP4: Short = 0x1D
        val ISO_TP4 =
            IpProtocol(IpProtocol.Companion.NUM_ISO_TP4)
        internal val NUM_NETBLT: Short = 0x1E
        val NETBLT =
            IpProtocol(IpProtocol.Companion.NUM_NETBLT)
        internal val NUM_MFE_NSP: Short = 0x1F
        val MFE_NSP =
            IpProtocol(IpProtocol.Companion.NUM_MFE_NSP)
        internal val NUM_MERIT_INP: Short = 0x20
        val MERIT_INP =
            IpProtocol(IpProtocol.Companion.NUM_MERIT_INP)
        internal val NUM_DCCP: Short = 0x21
        val DCCP = IpProtocol(IpProtocol.Companion.NUM_DCCP)
        internal val NUM_3PC: Short = 0x22
        val _3PC = IpProtocol(IpProtocol.Companion.NUM_3PC)
        internal val NUM_IDPR: Short = 0x23
        val IDPR = IpProtocol(IpProtocol.Companion.NUM_IDPR)
        internal val NUM_XTP: Short = 0x24
        val XTP = IpProtocol(IpProtocol.Companion.NUM_XTP)
        internal val NUM_DDP: Short = 0x25
        val DDP = IpProtocol(IpProtocol.Companion.NUM_DDP)
        internal val NUM_IDPR_CMTP: Short = 0x26
        val IDPR_CMTP =
            IpProtocol(IpProtocol.Companion.NUM_IDPR_CMTP)
        internal val NUM_TP_PP: Short = 0x27
        val TP_PP = IpProtocol(IpProtocol.Companion.NUM_TP_PP)
        internal val NUM_IL: Short = 0x28
        val IL = IpProtocol(IpProtocol.Companion.NUM_IL)
        internal val NUM_IPv6: Short = 0x29
        val IPv6 = IpProtocol(IpProtocol.Companion.NUM_IPv6)
        internal val NUM_SDRP: Short = 0x2A
        val SDRP = IpProtocol(IpProtocol.Companion.NUM_SDRP)
        internal val NUM_IPv6_ROUTE: Short = 0x2B
        val IPv6_ROUTE =
            IpProtocol(IpProtocol.Companion.NUM_IPv6_ROUTE)
        internal val NUM_IPv6_FRAG: Short = 0x2C
        val IPv6_FRAG =
            IpProtocol(IpProtocol.Companion.NUM_IPv6_FRAG)
        internal val NUM_IDRP: Short = 0x2D
        val IDRP = IpProtocol(IpProtocol.Companion.NUM_IDRP)
        internal val NUM_RSVP: Short = 0x2E
        val RSVP = IpProtocol(IpProtocol.Companion.NUM_RSVP)
        internal val NUM_GRE: Short = 0x2F
        val GRE = IpProtocol(IpProtocol.Companion.NUM_GRE)
        internal val NUM_MHRP: Short = 0x30
        val MHRP = IpProtocol(IpProtocol.Companion.NUM_MHRP)
        internal val NUM_BNA: Short = 0x31
        val BNA = IpProtocol(IpProtocol.Companion.NUM_BNA)
        internal val NUM_ESP: Short = 0x32
        val ESP = IpProtocol(IpProtocol.Companion.NUM_ESP)
        internal val NUM_AH: Short = 0x33
        val AH = IpProtocol(IpProtocol.Companion.NUM_AH)
        internal val NUM_I_NLSP: Short = 0x34
        val I_NLSP =
            IpProtocol(IpProtocol.Companion.NUM_I_NLSP)
        internal val NUM_SWIPE: Short = 0x35
        val SWIPE = IpProtocol(IpProtocol.Companion.NUM_SWIPE)
        internal val NUM_NARP: Short = 0x36
        val NARP = IpProtocol(IpProtocol.Companion.NUM_NARP)
        internal val NUM_MOBILE: Short = 0x37
        val MOBILE =
            IpProtocol(IpProtocol.Companion.NUM_MOBILE)
        internal val NUM_TLSP: Short = 0x38
        val TLSP = IpProtocol(IpProtocol.Companion.NUM_TLSP)
        internal val NUM_SKIP: Short = 0x39
        val SKIP = IpProtocol(IpProtocol.Companion.NUM_SKIP)
        internal val NUM_IPv6_ICMP: Short = 0x3A
        val IPv6_ICMP =
            IpProtocol(IpProtocol.Companion.NUM_IPv6_ICMP)
        internal val NUM_IPv6_NO_NXT: Short = 0x3B
        val IPv6_NO_NXT =
            IpProtocol(IpProtocol.Companion.NUM_IPv6_NO_NXT)
        internal val NUM_IPv6_OPTS: Short = 0x3C
        val IPv6_OPTS =
            IpProtocol(IpProtocol.Companion.NUM_IPv6_OPTS)
        internal val NUM_HOST_INTERNAL: Short = 0x3D
        val HOST_INTERNAL =
            IpProtocol(IpProtocol.Companion.NUM_HOST_INTERNAL)
        internal val NUM_CFTP: Short = 0x3E
        val CFTP = IpProtocol(IpProtocol.Companion.NUM_CFTP)
        internal val NUM_LOCAL_NET: Short = 0x3F
        val LOCAL_NET =
            IpProtocol(IpProtocol.Companion.NUM_LOCAL_NET)
        internal val NUM_SAT_EXPAK: Short = 0x40
        val SAT_EXPAK =
            IpProtocol(IpProtocol.Companion.NUM_SAT_EXPAK)
        internal val NUM_KRYPTOLAN: Short = 0x41
        val KRYPTOLAN =
            IpProtocol(IpProtocol.Companion.NUM_KRYPTOLAN)
        internal val NUM_RVD: Short = 0x42
        val RVD = IpProtocol(IpProtocol.Companion.NUM_RVD)
        internal val NUM_IPPC: Short = 0x43
        val IPPC = IpProtocol(IpProtocol.Companion.NUM_IPPC)
        internal val NUM_DIST_FS: Short = 0x44
        val DIST_FS =
            IpProtocol(IpProtocol.Companion.NUM_DIST_FS)
        internal val NUM_SAT_MON: Short = 0x45
        val SAT_MON =
            IpProtocol(IpProtocol.Companion.NUM_SAT_MON)
        internal val NUM_VISA: Short = 0x46
        val VISA = IpProtocol(IpProtocol.Companion.NUM_VISA)
        internal val NUM_IPCV: Short = 0x47
        val IPCV = IpProtocol(IpProtocol.Companion.NUM_IPCV)
        internal val NUM_CPNX: Short = 0x48
        val CPNX = IpProtocol(IpProtocol.Companion.NUM_CPNX)
        internal val NUM_CPHB: Short = 0x49
        val CPHB = IpProtocol(IpProtocol.Companion.NUM_CPHB)
        internal val NUM_WSN: Short = 0x4A
        val WSN = IpProtocol(IpProtocol.Companion.NUM_WSN)
        internal val NUM_PVP: Short = 0x4B
        val PVP = IpProtocol(IpProtocol.Companion.NUM_PVP)
        internal val NUM_BR_SAT_MON: Short = 0x4C
        val BR_SAT_MON =
            IpProtocol(IpProtocol.Companion.NUM_BR_SAT_MON)
        internal val NUM_SUN_ND: Short = 0x4D
        val SUN_ND =
            IpProtocol(IpProtocol.Companion.NUM_SUN_ND)
        internal val NUM_WB_MON: Short = 0x4E
        val WB_MON =
            IpProtocol(IpProtocol.Companion.NUM_WB_MON)
        internal val NUM_WB_EXPAK: Short = 0x4F
        val WB_EXPAK =
            IpProtocol(IpProtocol.Companion.NUM_WB_EXPAK)
        internal val NUM_ISO_IP: Short = 0x50
        val ISO_IP =
            IpProtocol(IpProtocol.Companion.NUM_ISO_IP)
        internal val NUM_VMTP: Short = 0x51
        val VMTP = IpProtocol(IpProtocol.Companion.NUM_VMTP)
        internal val NUM_SECURE_VMTP: Short = 0x52
        val SECURE_VMTP =
            IpProtocol(IpProtocol.Companion.NUM_SECURE_VMTP)
        internal val NUM_VINES: Short = 0x53
        val VINES = IpProtocol(IpProtocol.Companion.NUM_VINES)
        internal val NUM_TTP_IPTM: Short = 0x54
        val TTP_IPTM =
            IpProtocol(IpProtocol.Companion.NUM_TTP_IPTM)
        internal val NUM_NSFNET_IGP: Short = 0x55
        val NSFNET_IGP =
            IpProtocol(IpProtocol.Companion.NUM_NSFNET_IGP)
        internal val NUM_DGP: Short = 0x56
        val DGP = IpProtocol(IpProtocol.Companion.NUM_DGP)
        internal val NUM_TCF: Short = 0x57
        val TCF = IpProtocol(IpProtocol.Companion.NUM_TCF)
        internal val NUM_EIGRP: Short = 0x58
        val EIGRP = IpProtocol(IpProtocol.Companion.NUM_EIGRP)
        internal val NUM_OSPF: Short = 0x59
        val OSPF = IpProtocol(IpProtocol.Companion.NUM_OSPF)
        internal val NUM_Sprite_RPC: Short = 0x5A
        val Sprite_RPC =
            IpProtocol(IpProtocol.Companion.NUM_Sprite_RPC)
        internal val NUM_LARP: Short = 0x5B
        val LARP = IpProtocol(IpProtocol.Companion.NUM_LARP)
        internal val NUM_MTP: Short = 0x5C
        val MTP = IpProtocol(IpProtocol.Companion.NUM_MTP)
        internal val NUM_AX_25: Short = 0x5D
        val AX_25 = IpProtocol(IpProtocol.Companion.NUM_AX_25)
        internal val NUM_IPIP: Short = 0x5E
        val IPIP = IpProtocol(IpProtocol.Companion.NUM_IPIP)
        internal val NUM_MICP: Short = 0x5F
        val MICP = IpProtocol(IpProtocol.Companion.NUM_MICP)
        internal val NUM_SCC_SP: Short = 0x60
        val SCC_SP =
            IpProtocol(IpProtocol.Companion.NUM_SCC_SP)
        internal val NUM_ETHERIP: Short = 0x61
        val ETHERIP =
            IpProtocol(IpProtocol.Companion.NUM_ETHERIP)
        internal val NUM_ENCAP: Short = 0x62
        val ENCAP = IpProtocol(IpProtocol.Companion.NUM_ENCAP)
        internal val NUM_PRIVATE_ENCRYPT: Short = 0x63
        val PRIVATE_ENCRYPT =
            IpProtocol(IpProtocol.Companion.NUM_PRIVATE_ENCRYPT)
        internal val NUM_GMTP: Short = 0x64
        val GMTP = IpProtocol(IpProtocol.Companion.NUM_GMTP)
        internal val NUM_IFMP: Short = 0x65
        val IFMP = IpProtocol(IpProtocol.Companion.NUM_IFMP)
        internal val NUM_PNNI: Short = 0x66
        val PNNI = IpProtocol(IpProtocol.Companion.NUM_PNNI)
        internal val NUM_PIM: Short = 0x67
        val PIM = IpProtocol(IpProtocol.Companion.NUM_PIM)
        internal val NUM_ARIS: Short = 0x68
        val ARIS = IpProtocol(IpProtocol.Companion.NUM_ARIS)
        internal val NUM_SCPS: Short = 0x69
        val SCPS = IpProtocol(IpProtocol.Companion.NUM_SCPS)
        internal val NUM_QNX: Short = 0x6A
        val QNX = IpProtocol(IpProtocol.Companion.NUM_QNX)
        internal val NUM_A_N: Short = 0x6B
        val A_N = IpProtocol(IpProtocol.Companion.NUM_A_N)
        internal val NUM_IP_COMP: Short = 0x6C
        val IP_COMP =
            IpProtocol(IpProtocol.Companion.NUM_IP_COMP)
        internal val NUM_SNP: Short = 0x6D
        val SNP = IpProtocol(IpProtocol.Companion.NUM_SNP)
        internal val NUM_COMPAQ_PEER: Short = 0x6E
        val COMPAQ_PEER =
            IpProtocol(IpProtocol.Companion.NUM_COMPAQ_PEER)
        internal val NUM_IPX_IN_IP: Short = 0x6F
        val IPX_IN_IP =
            IpProtocol(IpProtocol.Companion.NUM_IPX_IN_IP)
        internal val NUM_VRRP: Short = 0x70
        val VRRP = IpProtocol(IpProtocol.Companion.NUM_VRRP)
        internal val NUM_PGM: Short = 0x71
        val PGM = IpProtocol(IpProtocol.Companion.NUM_PGM)
        internal val NUM_ZERO_HOP: Short = 0x72
        val ZERO_HOP =
            IpProtocol(IpProtocol.Companion.NUM_ZERO_HOP)
        internal val NUM_L2TP: Short = 0x73
        val L2TP = IpProtocol(IpProtocol.Companion.NUM_L2TP)
        internal val NUM_DDX: Short = 0x74
        val DDX = IpProtocol(IpProtocol.Companion.NUM_DDX)
        internal val NUM_IATP: Short = 0x75
        val IATP = IpProtocol(IpProtocol.Companion.NUM_IATP)
        internal val NUM_STP: Short = 0x76
        val STP = IpProtocol(IpProtocol.Companion.NUM_STP)
        internal val NUM_SRP: Short = 0x77
        val SRP = IpProtocol(IpProtocol.Companion.NUM_SRP)
        internal val NUM_UTI: Short = 0x78
        val UTI = IpProtocol(IpProtocol.Companion.NUM_UTI)
        internal val NUM_SMP: Short = 0x79
        val SMP = IpProtocol(IpProtocol.Companion.NUM_SMP)
        internal val NUM_SM: Short = 0x7A
        val SM = IpProtocol(IpProtocol.Companion.NUM_SM)
        internal val NUM_PTP: Short = 0x7B
        val PTP = IpProtocol(IpProtocol.Companion.NUM_PTP)
        internal val NUM_IS_IS_OVER_IPv4: Short = 0x7C
        val IS_IS_OVER_IPv4 =
            IpProtocol(IpProtocol.Companion.NUM_IS_IS_OVER_IPv4)
        internal val NUM_FIRE: Short = 0x7D
        val FIRE = IpProtocol(IpProtocol.Companion.NUM_FIRE)
        internal val NUM_CRTP: Short = 0x7E
        val CRTP = IpProtocol(IpProtocol.Companion.NUM_CRTP)
        internal val NUM_CRUDP: Short = 0x7F
        val CRUDP = IpProtocol(IpProtocol.Companion.NUM_CRUDP)
        internal val NUM_SSCOPMCE: Short = 0x80
        val SSCOPMCE =
            IpProtocol(IpProtocol.Companion.NUM_SSCOPMCE)
        internal val NUM_IPLT: Short = 0x81
        val IPLT = IpProtocol(IpProtocol.Companion.NUM_IPLT)
        internal val NUM_SPS: Short = 0x82
        val SPS = IpProtocol(IpProtocol.Companion.NUM_SPS)
        internal val NUM_PIPE: Short = 0x83
        val PIPE = IpProtocol(IpProtocol.Companion.NUM_PIPE)
        internal val NUM_SCTP: Short = 0x84
        val SCTP = IpProtocol(IpProtocol.Companion.NUM_SCTP)
        internal val NUM_FC: Short = 0x85
        val FC = IpProtocol(IpProtocol.Companion.NUM_FC)
        internal val NUM_RSVP_E2E_IGNORE: Short = 0x86
        val RSVP_E2E_IGNORE =
            IpProtocol(IpProtocol.Companion.NUM_RSVP_E2E_IGNORE)
        internal val NUM_MOBILITY_HEADER: Short = 0x87
        val MOBILITY_HEADER =
            IpProtocol(IpProtocol.Companion.NUM_MOBILITY_HEADER)
        internal val NUM_UDP_LITE: Short = 0x88
        val UDP_LITE =
            IpProtocol(IpProtocol.Companion.NUM_UDP_LITE)
        internal val NUM_MPLS_IN_IP: Short = 0x89
        val MPLS_IN_IP =
            IpProtocol(IpProtocol.Companion.NUM_MPLS_IN_IP)
        internal val NUM_MANET: Short = 0x8A
        val MANET = IpProtocol(IpProtocol.Companion.NUM_MANET)
        internal val NUM_HIP: Short = 0x8B
        val HIP = IpProtocol(IpProtocol.Companion.NUM_HIP)
        internal val NUM_SHIM6: Short = 0x8C
        val SHIM6 = IpProtocol(IpProtocol.Companion.NUM_SHIM6)

        fun of(proto: Short): IpProtocol {
            when (proto) {
                IpProtocol.Companion.NUM_HOPOPT -> return IpProtocol.Companion.HOPOPT
                IpProtocol.Companion.NUM_ICMP -> return IpProtocol.Companion.ICMP
                IpProtocol.Companion.NUM_IGMP -> return IpProtocol.Companion.IGMP
                IpProtocol.Companion.NUM_GGP -> return IpProtocol.Companion.GGP
                IpProtocol.Companion.NUM_IPv4 -> return IpProtocol.Companion.IPv4
                IpProtocol.Companion.NUM_ST -> return IpProtocol.Companion.ST
                IpProtocol.Companion.NUM_TCP -> return IpProtocol.Companion.TCP
                IpProtocol.Companion.NUM_CBT -> return IpProtocol.Companion.CBT
                IpProtocol.Companion.NUM_EGP -> return IpProtocol.Companion.EGP
                IpProtocol.Companion.NUM_IGP -> return IpProtocol.Companion.IGP
                IpProtocol.Companion.NUM_BBN_RCC_MON -> return IpProtocol.Companion.BBN_RCC_MON
                IpProtocol.Companion.NUM_NVP_II -> return IpProtocol.Companion.NVP_II
                IpProtocol.Companion.NUM_PUP -> return IpProtocol.Companion.PUP
                IpProtocol.Companion.NUM_ARGUS -> return IpProtocol.Companion.ARGUS
                IpProtocol.Companion.NUM_EMCON -> return IpProtocol.Companion.EMCON
                IpProtocol.Companion.NUM_XNET -> return IpProtocol.Companion.XNET
                IpProtocol.Companion.NUM_CHAOS -> return IpProtocol.Companion.CHAOS
                IpProtocol.Companion.NUM_UDP -> return IpProtocol.Companion.UDP
                IpProtocol.Companion.NUM_MUX -> return IpProtocol.Companion.MUX
                IpProtocol.Companion.NUM_DCN_MEAS -> return IpProtocol.Companion.DCN_MEAS
                IpProtocol.Companion.NUM_HMP -> return IpProtocol.Companion.HMP
                IpProtocol.Companion.NUM_PRM -> return IpProtocol.Companion.PRM
                IpProtocol.Companion.NUM_XNS_IDP -> return IpProtocol.Companion.XNS_IDP
                IpProtocol.Companion.NUM_TRUNK_1 -> return IpProtocol.Companion.TRUNK_1
                IpProtocol.Companion.NUM_TRUNK_2 -> return IpProtocol.Companion.TRUNK_2
                IpProtocol.Companion.NUM_LEAF_1 -> return IpProtocol.Companion.LEAF_1
                IpProtocol.Companion.NUM_LEAF_2 -> return IpProtocol.Companion.LEAF_2
                IpProtocol.Companion.NUM_RDP -> return IpProtocol.Companion.RDP
                IpProtocol.Companion.NUM_IRTP -> return IpProtocol.Companion.IRTP
                IpProtocol.Companion.NUM_ISO_TP4 -> return IpProtocol.Companion.ISO_TP4
                IpProtocol.Companion.NUM_NETBLT -> return IpProtocol.Companion.NETBLT
                IpProtocol.Companion.NUM_MFE_NSP -> return IpProtocol.Companion.MFE_NSP
                IpProtocol.Companion.NUM_MERIT_INP -> return IpProtocol.Companion.MERIT_INP
                IpProtocol.Companion.NUM_DCCP -> return IpProtocol.Companion.DCCP
                IpProtocol.Companion.NUM_3PC -> return IpProtocol.Companion._3PC
                IpProtocol.Companion.NUM_IDPR -> return IpProtocol.Companion.IDPR
                IpProtocol.Companion.NUM_XTP -> return IpProtocol.Companion.XTP
                IpProtocol.Companion.NUM_DDP -> return IpProtocol.Companion.DDP
                IpProtocol.Companion.NUM_IDPR_CMTP -> return IpProtocol.Companion.IDPR_CMTP
                IpProtocol.Companion.NUM_TP_PP -> return IpProtocol.Companion.TP_PP
                IpProtocol.Companion.NUM_IL -> return IpProtocol.Companion.IL
                IpProtocol.Companion.NUM_IPv6 -> return IpProtocol.Companion.IPv6
                IpProtocol.Companion.NUM_SDRP -> return IpProtocol.Companion.SDRP
                IpProtocol.Companion.NUM_IPv6_ROUTE -> return IpProtocol.Companion.IPv6_ROUTE
                IpProtocol.Companion.NUM_IPv6_FRAG -> return IpProtocol.Companion.IPv6_FRAG
                IpProtocol.Companion.NUM_IDRP -> return IpProtocol.Companion.IDRP
                IpProtocol.Companion.NUM_RSVP -> return IpProtocol.Companion.RSVP
                IpProtocol.Companion.NUM_GRE -> return IpProtocol.Companion.GRE
                IpProtocol.Companion.NUM_MHRP -> return IpProtocol.Companion.MHRP
                IpProtocol.Companion.NUM_BNA -> return IpProtocol.Companion.BNA
                IpProtocol.Companion.NUM_ESP -> return IpProtocol.Companion.ESP
                IpProtocol.Companion.NUM_AH -> return IpProtocol.Companion.AH
                IpProtocol.Companion.NUM_I_NLSP -> return IpProtocol.Companion.I_NLSP
                IpProtocol.Companion.NUM_SWIPE -> return IpProtocol.Companion.SWIPE
                IpProtocol.Companion.NUM_NARP -> return IpProtocol.Companion.NARP
                IpProtocol.Companion.NUM_MOBILE -> return IpProtocol.Companion.MOBILE
                IpProtocol.Companion.NUM_TLSP -> return IpProtocol.Companion.TLSP
                IpProtocol.Companion.NUM_SKIP -> return IpProtocol.Companion.SKIP
                IpProtocol.Companion.NUM_IPv6_ICMP -> return IpProtocol.Companion.IPv6_ICMP
                IpProtocol.Companion.NUM_IPv6_NO_NXT -> return IpProtocol.Companion.IPv6_NO_NXT
                IpProtocol.Companion.NUM_IPv6_OPTS -> return IpProtocol.Companion.IPv6_OPTS
                IpProtocol.Companion.NUM_HOST_INTERNAL -> return IpProtocol.Companion.HOST_INTERNAL
                IpProtocol.Companion.NUM_CFTP -> return IpProtocol.Companion.CFTP
                IpProtocol.Companion.NUM_LOCAL_NET -> return IpProtocol.Companion.LOCAL_NET
                IpProtocol.Companion.NUM_SAT_EXPAK -> return IpProtocol.Companion.SAT_EXPAK
                IpProtocol.Companion.NUM_KRYPTOLAN -> return IpProtocol.Companion.KRYPTOLAN
                IpProtocol.Companion.NUM_RVD -> return IpProtocol.Companion.RVD
                IpProtocol.Companion.NUM_IPPC -> return IpProtocol.Companion.IPPC
                IpProtocol.Companion.NUM_DIST_FS -> return IpProtocol.Companion.DIST_FS
                IpProtocol.Companion.NUM_SAT_MON -> return IpProtocol.Companion.SAT_MON
                IpProtocol.Companion.NUM_VISA -> return IpProtocol.Companion.VISA
                IpProtocol.Companion.NUM_IPCV -> return IpProtocol.Companion.IPCV
                IpProtocol.Companion.NUM_CPNX -> return IpProtocol.Companion.CPNX
                IpProtocol.Companion.NUM_CPHB -> return IpProtocol.Companion.CPHB
                IpProtocol.Companion.NUM_WSN -> return IpProtocol.Companion.WSN
                IpProtocol.Companion.NUM_PVP -> return IpProtocol.Companion.PVP
                IpProtocol.Companion.NUM_BR_SAT_MON -> return IpProtocol.Companion.BR_SAT_MON
                IpProtocol.Companion.NUM_SUN_ND -> return IpProtocol.Companion.SUN_ND
                IpProtocol.Companion.NUM_WB_MON -> return IpProtocol.Companion.WB_MON
                IpProtocol.Companion.NUM_WB_EXPAK -> return IpProtocol.Companion.WB_EXPAK
                IpProtocol.Companion.NUM_ISO_IP -> return IpProtocol.Companion.ISO_IP
                IpProtocol.Companion.NUM_VMTP -> return IpProtocol.Companion.VMTP
                IpProtocol.Companion.NUM_SECURE_VMTP -> return IpProtocol.Companion.SECURE_VMTP
                IpProtocol.Companion.NUM_VINES -> return IpProtocol.Companion.VINES
                IpProtocol.Companion.NUM_TTP_IPTM -> return IpProtocol.Companion.TTP_IPTM
                IpProtocol.Companion.NUM_NSFNET_IGP -> return IpProtocol.Companion.NSFNET_IGP
                IpProtocol.Companion.NUM_DGP -> return IpProtocol.Companion.DGP
                IpProtocol.Companion.NUM_TCF -> return IpProtocol.Companion.TCF
                IpProtocol.Companion.NUM_EIGRP -> return IpProtocol.Companion.EIGRP
                IpProtocol.Companion.NUM_OSPF -> return IpProtocol.Companion.OSPF
                IpProtocol.Companion.NUM_Sprite_RPC -> return IpProtocol.Companion.Sprite_RPC
                IpProtocol.Companion.NUM_LARP -> return IpProtocol.Companion.LARP
                IpProtocol.Companion.NUM_MTP -> return IpProtocol.Companion.MTP
                IpProtocol.Companion.NUM_AX_25 -> return IpProtocol.Companion.AX_25
                IpProtocol.Companion.NUM_IPIP -> return IpProtocol.Companion.IPIP
                IpProtocol.Companion.NUM_MICP -> return IpProtocol.Companion.MICP
                IpProtocol.Companion.NUM_SCC_SP -> return IpProtocol.Companion.SCC_SP
                IpProtocol.Companion.NUM_ETHERIP -> return IpProtocol.Companion.ETHERIP
                IpProtocol.Companion.NUM_ENCAP -> return IpProtocol.Companion.ENCAP
                IpProtocol.Companion.NUM_PRIVATE_ENCRYPT -> return IpProtocol.Companion.PRIVATE_ENCRYPT
                IpProtocol.Companion.NUM_GMTP -> return IpProtocol.Companion.GMTP
                IpProtocol.Companion.NUM_IFMP -> return IpProtocol.Companion.IFMP
                IpProtocol.Companion.NUM_PNNI -> return IpProtocol.Companion.PNNI
                IpProtocol.Companion.NUM_PIM -> return IpProtocol.Companion.PIM
                IpProtocol.Companion.NUM_ARIS -> return IpProtocol.Companion.ARIS
                IpProtocol.Companion.NUM_SCPS -> return IpProtocol.Companion.SCPS
                IpProtocol.Companion.NUM_QNX -> return IpProtocol.Companion.QNX
                IpProtocol.Companion.NUM_A_N -> return IpProtocol.Companion.A_N
                IpProtocol.Companion.NUM_IP_COMP -> return IpProtocol.Companion.IP_COMP
                IpProtocol.Companion.NUM_SNP -> return IpProtocol.Companion.SNP
                IpProtocol.Companion.NUM_COMPAQ_PEER -> return IpProtocol.Companion.COMPAQ_PEER
                IpProtocol.Companion.NUM_IPX_IN_IP -> return IpProtocol.Companion.IPX_IN_IP
                IpProtocol.Companion.NUM_VRRP -> return IpProtocol.Companion.VRRP
                IpProtocol.Companion.NUM_PGM -> return IpProtocol.Companion.PGM
                IpProtocol.Companion.NUM_ZERO_HOP -> return IpProtocol.Companion.ZERO_HOP
                IpProtocol.Companion.NUM_L2TP -> return IpProtocol.Companion.L2TP
                IpProtocol.Companion.NUM_DDX -> return IpProtocol.Companion.DDX
                IpProtocol.Companion.NUM_IATP -> return IpProtocol.Companion.IATP
                IpProtocol.Companion.NUM_STP -> return IpProtocol.Companion.STP
                IpProtocol.Companion.NUM_SRP -> return IpProtocol.Companion.SRP
                IpProtocol.Companion.NUM_UTI -> return IpProtocol.Companion.UTI
                IpProtocol.Companion.NUM_SMP -> return IpProtocol.Companion.SMP
                IpProtocol.Companion.NUM_SM -> return IpProtocol.Companion.SM
                IpProtocol.Companion.NUM_PTP -> return IpProtocol.Companion.PTP
                IpProtocol.Companion.NUM_IS_IS_OVER_IPv4 -> return IpProtocol.Companion.IS_IS_OVER_IPv4
                IpProtocol.Companion.NUM_FIRE -> return IpProtocol.Companion.FIRE
                IpProtocol.Companion.NUM_CRTP -> return IpProtocol.Companion.CRTP
                IpProtocol.Companion.NUM_CRUDP -> return IpProtocol.Companion.CRUDP
                IpProtocol.Companion.NUM_SSCOPMCE -> return IpProtocol.Companion.SSCOPMCE
                IpProtocol.Companion.NUM_IPLT -> return IpProtocol.Companion.IPLT
                IpProtocol.Companion.NUM_SPS -> return IpProtocol.Companion.SPS
                IpProtocol.Companion.NUM_PIPE -> return IpProtocol.Companion.PIPE
                IpProtocol.Companion.NUM_SCTP -> return IpProtocol.Companion.SCTP
                IpProtocol.Companion.NUM_FC -> return IpProtocol.Companion.FC
                IpProtocol.Companion.NUM_RSVP_E2E_IGNORE -> return IpProtocol.Companion.RSVP_E2E_IGNORE
                IpProtocol.Companion.NUM_MOBILITY_HEADER -> return IpProtocol.Companion.MOBILITY_HEADER
                IpProtocol.Companion.NUM_UDP_LITE -> return IpProtocol.Companion.UDP_LITE
                IpProtocol.Companion.NUM_MPLS_IN_IP -> return IpProtocol.Companion.MPLS_IN_IP
                IpProtocol.Companion.NUM_MANET -> return IpProtocol.Companion.MANET
                IpProtocol.Companion.NUM_HIP -> return IpProtocol.Companion.HIP
                IpProtocol.Companion.NUM_SHIM6 -> return IpProtocol.Companion.SHIM6
                else -> return if (proto >= IpProtocol.Companion.MAX_PROTO) {
                    throw IllegalArgumentException("Illegal IP protocol number: $proto")
                } else {
                    IpProtocol(proto)
                }
            }
        }
    }
}
