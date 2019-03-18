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
package sssemil.com.bridge.packet.types

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
        val HOPOPT = IpProtocol(NUM_HOPOPT)
        val NONE = HOPOPT
        val NO_MASK = HOPOPT
        internal val NUM_ICMP: Short = 0x01
        val ICMP = IpProtocol(NUM_ICMP)
        internal val NUM_IGMP: Short = 0x02
        val IGMP = IpProtocol(NUM_IGMP)
        internal val NUM_GGP: Short = 0x03
        val GGP = IpProtocol(NUM_GGP)
        internal val NUM_IPv4: Short = 0x04
        val IPv4 = IpProtocol(NUM_IPv4)
        internal val NUM_ST: Short = 0x05
        val ST = IpProtocol(NUM_ST)
        internal val NUM_TCP: Short = 0x06
        val TCP = IpProtocol(NUM_TCP)
        internal val NUM_CBT: Short = 0x07
        val CBT = IpProtocol(NUM_CBT)
        internal val NUM_EGP: Short = 0x08
        val EGP = IpProtocol(NUM_EGP)
        internal val NUM_IGP: Short = 0x09
        val IGP = IpProtocol(NUM_IGP)
        internal val NUM_BBN_RCC_MON: Short = 0x0A
        val BBN_RCC_MON = IpProtocol(NUM_BBN_RCC_MON)
        internal val NUM_NVP_II: Short = 0x0B
        val NVP_II = IpProtocol(NUM_NVP_II)
        internal val NUM_PUP: Short = 0x0C
        val PUP = IpProtocol(NUM_PUP)
        internal val NUM_ARGUS: Short = 0x0D
        val ARGUS = IpProtocol(NUM_ARGUS)
        internal val NUM_EMCON: Short = 0x0E
        val EMCON = IpProtocol(NUM_EMCON)
        internal val NUM_XNET: Short = 0x0F
        val XNET = IpProtocol(NUM_XNET)
        internal val NUM_CHAOS: Short = 0x10
        val CHAOS = IpProtocol(NUM_CHAOS)
        internal val NUM_UDP: Short = 0x11
        val UDP = IpProtocol(NUM_UDP)
        internal val NUM_MUX: Short = 0x12
        val MUX = IpProtocol(NUM_MUX)
        internal val NUM_DCN_MEAS: Short = 0x13
        val DCN_MEAS = IpProtocol(NUM_DCN_MEAS)
        internal val NUM_HMP: Short = 0x14
        val HMP = IpProtocol(NUM_HMP)
        internal val NUM_PRM: Short = 0x15
        val PRM = IpProtocol(NUM_PRM)
        internal val NUM_XNS_IDP: Short = 0x16
        val XNS_IDP = IpProtocol(NUM_XNS_IDP)
        internal val NUM_TRUNK_1: Short = 0x17
        val TRUNK_1 = IpProtocol(NUM_TRUNK_1)
        internal val NUM_TRUNK_2: Short = 0x18
        val TRUNK_2 = IpProtocol(NUM_TRUNK_2)
        internal val NUM_LEAF_1: Short = 0x19
        val LEAF_1 = IpProtocol(NUM_LEAF_1)
        internal val NUM_LEAF_2: Short = 0x1A
        val LEAF_2 = IpProtocol(NUM_LEAF_2)
        internal val NUM_RDP: Short = 0x1B
        val RDP = IpProtocol(NUM_RDP)
        internal val NUM_IRTP: Short = 0x1C
        val IRTP = IpProtocol(NUM_IRTP)
        internal val NUM_ISO_TP4: Short = 0x1D
        val ISO_TP4 = IpProtocol(NUM_ISO_TP4)
        internal val NUM_NETBLT: Short = 0x1E
        val NETBLT = IpProtocol(NUM_NETBLT)
        internal val NUM_MFE_NSP: Short = 0x1F
        val MFE_NSP = IpProtocol(NUM_MFE_NSP)
        internal val NUM_MERIT_INP: Short = 0x20
        val MERIT_INP = IpProtocol(NUM_MERIT_INP)
        internal val NUM_DCCP: Short = 0x21
        val DCCP = IpProtocol(NUM_DCCP)
        internal val NUM_3PC: Short = 0x22
        val _3PC = IpProtocol(NUM_3PC)
        internal val NUM_IDPR: Short = 0x23
        val IDPR = IpProtocol(NUM_IDPR)
        internal val NUM_XTP: Short = 0x24
        val XTP = IpProtocol(NUM_XTP)
        internal val NUM_DDP: Short = 0x25
        val DDP = IpProtocol(NUM_DDP)
        internal val NUM_IDPR_CMTP: Short = 0x26
        val IDPR_CMTP = IpProtocol(NUM_IDPR_CMTP)
        internal val NUM_TP_PP: Short = 0x27
        val TP_PP = IpProtocol(NUM_TP_PP)
        internal val NUM_IL: Short = 0x28
        val IL = IpProtocol(NUM_IL)
        internal val NUM_IPv6: Short = 0x29
        val IPv6 = IpProtocol(NUM_IPv6)
        internal val NUM_SDRP: Short = 0x2A
        val SDRP = IpProtocol(NUM_SDRP)
        internal val NUM_IPv6_ROUTE: Short = 0x2B
        val IPv6_ROUTE = IpProtocol(NUM_IPv6_ROUTE)
        internal val NUM_IPv6_FRAG: Short = 0x2C
        val IPv6_FRAG = IpProtocol(NUM_IPv6_FRAG)
        internal val NUM_IDRP: Short = 0x2D
        val IDRP = IpProtocol(NUM_IDRP)
        internal val NUM_RSVP: Short = 0x2E
        val RSVP = IpProtocol(NUM_RSVP)
        internal val NUM_GRE: Short = 0x2F
        val GRE = IpProtocol(NUM_GRE)
        internal val NUM_MHRP: Short = 0x30
        val MHRP = IpProtocol(NUM_MHRP)
        internal val NUM_BNA: Short = 0x31
        val BNA = IpProtocol(NUM_BNA)
        internal val NUM_ESP: Short = 0x32
        val ESP = IpProtocol(NUM_ESP)
        internal val NUM_AH: Short = 0x33
        val AH = IpProtocol(NUM_AH)
        internal val NUM_I_NLSP: Short = 0x34
        val I_NLSP = IpProtocol(NUM_I_NLSP)
        internal val NUM_SWIPE: Short = 0x35
        val SWIPE = IpProtocol(NUM_SWIPE)
        internal val NUM_NARP: Short = 0x36
        val NARP = IpProtocol(NUM_NARP)
        internal val NUM_MOBILE: Short = 0x37
        val MOBILE = IpProtocol(NUM_MOBILE)
        internal val NUM_TLSP: Short = 0x38
        val TLSP = IpProtocol(NUM_TLSP)
        internal val NUM_SKIP: Short = 0x39
        val SKIP = IpProtocol(NUM_SKIP)
        internal val NUM_IPv6_ICMP: Short = 0x3A
        val IPv6_ICMP = IpProtocol(NUM_IPv6_ICMP)
        internal val NUM_IPv6_NO_NXT: Short = 0x3B
        val IPv6_NO_NXT = IpProtocol(NUM_IPv6_NO_NXT)
        internal val NUM_IPv6_OPTS: Short = 0x3C
        val IPv6_OPTS = IpProtocol(NUM_IPv6_OPTS)
        internal val NUM_HOST_INTERNAL: Short = 0x3D
        val HOST_INTERNAL = IpProtocol(NUM_HOST_INTERNAL)
        internal val NUM_CFTP: Short = 0x3E
        val CFTP = IpProtocol(NUM_CFTP)
        internal val NUM_LOCAL_NET: Short = 0x3F
        val LOCAL_NET = IpProtocol(NUM_LOCAL_NET)
        internal val NUM_SAT_EXPAK: Short = 0x40
        val SAT_EXPAK = IpProtocol(NUM_SAT_EXPAK)
        internal val NUM_KRYPTOLAN: Short = 0x41
        val KRYPTOLAN = IpProtocol(NUM_KRYPTOLAN)
        internal val NUM_RVD: Short = 0x42
        val RVD = IpProtocol(NUM_RVD)
        internal val NUM_IPPC: Short = 0x43
        val IPPC = IpProtocol(NUM_IPPC)
        internal val NUM_DIST_FS: Short = 0x44
        val DIST_FS = IpProtocol(NUM_DIST_FS)
        internal val NUM_SAT_MON: Short = 0x45
        val SAT_MON = IpProtocol(NUM_SAT_MON)
        internal val NUM_VISA: Short = 0x46
        val VISA = IpProtocol(NUM_VISA)
        internal val NUM_IPCV: Short = 0x47
        val IPCV = IpProtocol(NUM_IPCV)
        internal val NUM_CPNX: Short = 0x48
        val CPNX = IpProtocol(NUM_CPNX)
        internal val NUM_CPHB: Short = 0x49
        val CPHB = IpProtocol(NUM_CPHB)
        internal val NUM_WSN: Short = 0x4A
        val WSN = IpProtocol(NUM_WSN)
        internal val NUM_PVP: Short = 0x4B
        val PVP = IpProtocol(NUM_PVP)
        internal val NUM_BR_SAT_MON: Short = 0x4C
        val BR_SAT_MON = IpProtocol(NUM_BR_SAT_MON)
        internal val NUM_SUN_ND: Short = 0x4D
        val SUN_ND = IpProtocol(NUM_SUN_ND)
        internal val NUM_WB_MON: Short = 0x4E
        val WB_MON = IpProtocol(NUM_WB_MON)
        internal val NUM_WB_EXPAK: Short = 0x4F
        val WB_EXPAK = IpProtocol(NUM_WB_EXPAK)
        internal val NUM_ISO_IP: Short = 0x50
        val ISO_IP = IpProtocol(NUM_ISO_IP)
        internal val NUM_VMTP: Short = 0x51
        val VMTP = IpProtocol(NUM_VMTP)
        internal val NUM_SECURE_VMTP: Short = 0x52
        val SECURE_VMTP = IpProtocol(NUM_SECURE_VMTP)
        internal val NUM_VINES: Short = 0x53
        val VINES = IpProtocol(NUM_VINES)
        internal val NUM_TTP_IPTM: Short = 0x54
        val TTP_IPTM = IpProtocol(NUM_TTP_IPTM)
        internal val NUM_NSFNET_IGP: Short = 0x55
        val NSFNET_IGP = IpProtocol(NUM_NSFNET_IGP)
        internal val NUM_DGP: Short = 0x56
        val DGP = IpProtocol(NUM_DGP)
        internal val NUM_TCF: Short = 0x57
        val TCF = IpProtocol(NUM_TCF)
        internal val NUM_EIGRP: Short = 0x58
        val EIGRP = IpProtocol(NUM_EIGRP)
        internal val NUM_OSPF: Short = 0x59
        val OSPF = IpProtocol(NUM_OSPF)
        internal val NUM_Sprite_RPC: Short = 0x5A
        val Sprite_RPC = IpProtocol(NUM_Sprite_RPC)
        internal val NUM_LARP: Short = 0x5B
        val LARP = IpProtocol(NUM_LARP)
        internal val NUM_MTP: Short = 0x5C
        val MTP = IpProtocol(NUM_MTP)
        internal val NUM_AX_25: Short = 0x5D
        val AX_25 = IpProtocol(NUM_AX_25)
        internal val NUM_IPIP: Short = 0x5E
        val IPIP = IpProtocol(NUM_IPIP)
        internal val NUM_MICP: Short = 0x5F
        val MICP = IpProtocol(NUM_MICP)
        internal val NUM_SCC_SP: Short = 0x60
        val SCC_SP = IpProtocol(NUM_SCC_SP)
        internal val NUM_ETHERIP: Short = 0x61
        val ETHERIP = IpProtocol(NUM_ETHERIP)
        internal val NUM_ENCAP: Short = 0x62
        val ENCAP = IpProtocol(NUM_ENCAP)
        internal val NUM_PRIVATE_ENCRYPT: Short = 0x63
        val PRIVATE_ENCRYPT = IpProtocol(NUM_PRIVATE_ENCRYPT)
        internal val NUM_GMTP: Short = 0x64
        val GMTP = IpProtocol(NUM_GMTP)
        internal val NUM_IFMP: Short = 0x65
        val IFMP = IpProtocol(NUM_IFMP)
        internal val NUM_PNNI: Short = 0x66
        val PNNI = IpProtocol(NUM_PNNI)
        internal val NUM_PIM: Short = 0x67
        val PIM = IpProtocol(NUM_PIM)
        internal val NUM_ARIS: Short = 0x68
        val ARIS = IpProtocol(NUM_ARIS)
        internal val NUM_SCPS: Short = 0x69
        val SCPS = IpProtocol(NUM_SCPS)
        internal val NUM_QNX: Short = 0x6A
        val QNX = IpProtocol(NUM_QNX)
        internal val NUM_A_N: Short = 0x6B
        val A_N = IpProtocol(NUM_A_N)
        internal val NUM_IP_COMP: Short = 0x6C
        val IP_COMP = IpProtocol(NUM_IP_COMP)
        internal val NUM_SNP: Short = 0x6D
        val SNP = IpProtocol(NUM_SNP)
        internal val NUM_COMPAQ_PEER: Short = 0x6E
        val COMPAQ_PEER = IpProtocol(NUM_COMPAQ_PEER)
        internal val NUM_IPX_IN_IP: Short = 0x6F
        val IPX_IN_IP = IpProtocol(NUM_IPX_IN_IP)
        internal val NUM_VRRP: Short = 0x70
        val VRRP = IpProtocol(NUM_VRRP)
        internal val NUM_PGM: Short = 0x71
        val PGM = IpProtocol(NUM_PGM)
        internal val NUM_ZERO_HOP: Short = 0x72
        val ZERO_HOP = IpProtocol(NUM_ZERO_HOP)
        internal val NUM_L2TP: Short = 0x73
        val L2TP = IpProtocol(NUM_L2TP)
        internal val NUM_DDX: Short = 0x74
        val DDX = IpProtocol(NUM_DDX)
        internal val NUM_IATP: Short = 0x75
        val IATP = IpProtocol(NUM_IATP)
        internal val NUM_STP: Short = 0x76
        val STP = IpProtocol(NUM_STP)
        internal val NUM_SRP: Short = 0x77
        val SRP = IpProtocol(NUM_SRP)
        internal val NUM_UTI: Short = 0x78
        val UTI = IpProtocol(NUM_UTI)
        internal val NUM_SMP: Short = 0x79
        val SMP = IpProtocol(NUM_SMP)
        internal val NUM_SM: Short = 0x7A
        val SM = IpProtocol(NUM_SM)
        internal val NUM_PTP: Short = 0x7B
        val PTP = IpProtocol(NUM_PTP)
        internal val NUM_IS_IS_OVER_IPv4: Short = 0x7C
        val IS_IS_OVER_IPv4 = IpProtocol(NUM_IS_IS_OVER_IPv4)
        internal val NUM_FIRE: Short = 0x7D
        val FIRE = IpProtocol(NUM_FIRE)
        internal val NUM_CRTP: Short = 0x7E
        val CRTP = IpProtocol(NUM_CRTP)
        internal val NUM_CRUDP: Short = 0x7F
        val CRUDP = IpProtocol(NUM_CRUDP)
        internal val NUM_SSCOPMCE: Short = 0x80
        val SSCOPMCE = IpProtocol(NUM_SSCOPMCE)
        internal val NUM_IPLT: Short = 0x81
        val IPLT = IpProtocol(NUM_IPLT)
        internal val NUM_SPS: Short = 0x82
        val SPS = IpProtocol(NUM_SPS)
        internal val NUM_PIPE: Short = 0x83
        val PIPE = IpProtocol(NUM_PIPE)
        internal val NUM_SCTP: Short = 0x84
        val SCTP = IpProtocol(NUM_SCTP)
        internal val NUM_FC: Short = 0x85
        val FC = IpProtocol(NUM_FC)
        internal val NUM_RSVP_E2E_IGNORE: Short = 0x86
        val RSVP_E2E_IGNORE = IpProtocol(NUM_RSVP_E2E_IGNORE)
        internal val NUM_MOBILITY_HEADER: Short = 0x87
        val MOBILITY_HEADER = IpProtocol(NUM_MOBILITY_HEADER)
        internal val NUM_UDP_LITE: Short = 0x88
        val UDP_LITE = IpProtocol(NUM_UDP_LITE)
        internal val NUM_MPLS_IN_IP: Short = 0x89
        val MPLS_IN_IP = IpProtocol(NUM_MPLS_IN_IP)
        internal val NUM_MANET: Short = 0x8A
        val MANET = IpProtocol(NUM_MANET)
        internal val NUM_HIP: Short = 0x8B
        val HIP = IpProtocol(NUM_HIP)
        internal val NUM_SHIM6: Short = 0x8C
        val SHIM6 = IpProtocol(NUM_SHIM6)

        fun of(proto: Short): IpProtocol {
            when (proto) {
                NUM_HOPOPT -> return HOPOPT
                NUM_ICMP -> return ICMP
                NUM_IGMP -> return IGMP
                NUM_GGP -> return GGP
                NUM_IPv4 -> return IPv4
                NUM_ST -> return ST
                NUM_TCP -> return TCP
                NUM_CBT -> return CBT
                NUM_EGP -> return EGP
                NUM_IGP -> return IGP
                NUM_BBN_RCC_MON -> return BBN_RCC_MON
                NUM_NVP_II -> return NVP_II
                NUM_PUP -> return PUP
                NUM_ARGUS -> return ARGUS
                NUM_EMCON -> return EMCON
                NUM_XNET -> return XNET
                NUM_CHAOS -> return CHAOS
                NUM_UDP -> return UDP
                NUM_MUX -> return MUX
                NUM_DCN_MEAS -> return DCN_MEAS
                NUM_HMP -> return HMP
                NUM_PRM -> return PRM
                NUM_XNS_IDP -> return XNS_IDP
                NUM_TRUNK_1 -> return TRUNK_1
                NUM_TRUNK_2 -> return TRUNK_2
                NUM_LEAF_1 -> return LEAF_1
                NUM_LEAF_2 -> return LEAF_2
                NUM_RDP -> return RDP
                NUM_IRTP -> return IRTP
                NUM_ISO_TP4 -> return ISO_TP4
                NUM_NETBLT -> return NETBLT
                NUM_MFE_NSP -> return MFE_NSP
                NUM_MERIT_INP -> return MERIT_INP
                NUM_DCCP -> return DCCP
                NUM_3PC -> return _3PC
                NUM_IDPR -> return IDPR
                NUM_XTP -> return XTP
                NUM_DDP -> return DDP
                NUM_IDPR_CMTP -> return IDPR_CMTP
                NUM_TP_PP -> return TP_PP
                NUM_IL -> return IL
                NUM_IPv6 -> return IPv6
                NUM_SDRP -> return SDRP
                NUM_IPv6_ROUTE -> return IPv6_ROUTE
                NUM_IPv6_FRAG -> return IPv6_FRAG
                NUM_IDRP -> return IDRP
                NUM_RSVP -> return RSVP
                NUM_GRE -> return GRE
                NUM_MHRP -> return MHRP
                NUM_BNA -> return BNA
                NUM_ESP -> return ESP
                NUM_AH -> return AH
                NUM_I_NLSP -> return I_NLSP
                NUM_SWIPE -> return SWIPE
                NUM_NARP -> return NARP
                NUM_MOBILE -> return MOBILE
                NUM_TLSP -> return TLSP
                NUM_SKIP -> return SKIP
                NUM_IPv6_ICMP -> return IPv6_ICMP
                NUM_IPv6_NO_NXT -> return IPv6_NO_NXT
                NUM_IPv6_OPTS -> return IPv6_OPTS
                NUM_HOST_INTERNAL -> return HOST_INTERNAL
                NUM_CFTP -> return CFTP
                NUM_LOCAL_NET -> return LOCAL_NET
                NUM_SAT_EXPAK -> return SAT_EXPAK
                NUM_KRYPTOLAN -> return KRYPTOLAN
                NUM_RVD -> return RVD
                NUM_IPPC -> return IPPC
                NUM_DIST_FS -> return DIST_FS
                NUM_SAT_MON -> return SAT_MON
                NUM_VISA -> return VISA
                NUM_IPCV -> return IPCV
                NUM_CPNX -> return CPNX
                NUM_CPHB -> return CPHB
                NUM_WSN -> return WSN
                NUM_PVP -> return PVP
                NUM_BR_SAT_MON -> return BR_SAT_MON
                NUM_SUN_ND -> return SUN_ND
                NUM_WB_MON -> return WB_MON
                NUM_WB_EXPAK -> return WB_EXPAK
                NUM_ISO_IP -> return ISO_IP
                NUM_VMTP -> return VMTP
                NUM_SECURE_VMTP -> return SECURE_VMTP
                NUM_VINES -> return VINES
                NUM_TTP_IPTM -> return TTP_IPTM
                NUM_NSFNET_IGP -> return NSFNET_IGP
                NUM_DGP -> return DGP
                NUM_TCF -> return TCF
                NUM_EIGRP -> return EIGRP
                NUM_OSPF -> return OSPF
                NUM_Sprite_RPC -> return Sprite_RPC
                NUM_LARP -> return LARP
                NUM_MTP -> return MTP
                NUM_AX_25 -> return AX_25
                NUM_IPIP -> return IPIP
                NUM_MICP -> return MICP
                NUM_SCC_SP -> return SCC_SP
                NUM_ETHERIP -> return ETHERIP
                NUM_ENCAP -> return ENCAP
                NUM_PRIVATE_ENCRYPT -> return PRIVATE_ENCRYPT
                NUM_GMTP -> return GMTP
                NUM_IFMP -> return IFMP
                NUM_PNNI -> return PNNI
                NUM_PIM -> return PIM
                NUM_ARIS -> return ARIS
                NUM_SCPS -> return SCPS
                NUM_QNX -> return QNX
                NUM_A_N -> return A_N
                NUM_IP_COMP -> return IP_COMP
                NUM_SNP -> return SNP
                NUM_COMPAQ_PEER -> return COMPAQ_PEER
                NUM_IPX_IN_IP -> return IPX_IN_IP
                NUM_VRRP -> return VRRP
                NUM_PGM -> return PGM
                NUM_ZERO_HOP -> return ZERO_HOP
                NUM_L2TP -> return L2TP
                NUM_DDX -> return DDX
                NUM_IATP -> return IATP
                NUM_STP -> return STP
                NUM_SRP -> return SRP
                NUM_UTI -> return UTI
                NUM_SMP -> return SMP
                NUM_SM -> return SM
                NUM_PTP -> return PTP
                NUM_IS_IS_OVER_IPv4 -> return IS_IS_OVER_IPv4
                NUM_FIRE -> return FIRE
                NUM_CRTP -> return CRTP
                NUM_CRUDP -> return CRUDP
                NUM_SSCOPMCE -> return SSCOPMCE
                NUM_IPLT -> return IPLT
                NUM_SPS -> return SPS
                NUM_PIPE -> return PIPE
                NUM_SCTP -> return SCTP
                NUM_FC -> return FC
                NUM_RSVP_E2E_IGNORE -> return RSVP_E2E_IGNORE
                NUM_MOBILITY_HEADER -> return MOBILITY_HEADER
                NUM_UDP_LITE -> return UDP_LITE
                NUM_MPLS_IN_IP -> return MPLS_IN_IP
                NUM_MANET -> return MANET
                NUM_HIP -> return HIP
                NUM_SHIM6 -> return SHIM6
                else -> return if (proto >= MAX_PROTO) {
                    throw IllegalArgumentException("Illegal IP protocol number: $proto")
                } else {
                    IpProtocol(proto)
                }
            }
        }
    }
}
