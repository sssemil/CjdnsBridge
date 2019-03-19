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
package sssemil.com.net.packet

import sssemil.com.common.util.Logger
import sssemil.com.net.packet.icmpv6.EchoReplyMessage
import sssemil.com.net.packet.icmpv6.EchoRequestMessage
import sssemil.com.net.packet.types.IpProtocol
import sssemil.com.net.util.InternetChecksum
import java.nio.ByteBuffer

/**
 * Implements ICMPv6 packet format.
 */
data class ICMPv6(
    var icmpType: UByte = 0u,
    var icmpCode: UByte = 0u,
    var checksum: Short = 0,
    override var payload: IPacket? = null
) : BasePacket() {

    override fun resetChecksum() {
        this.checksum = 0
        super.resetChecksum()
    }

    /**
     * Serializes the packet. Will compute and set the following fields if they
     * are set to specific values at the time serialize is called:
     * -checksum : 0
     * -length : 0
     */
    override fun serialize(): ByteArray {
        var length = 4
        val payloadData = payload?.let {
            it.parent = this
            val data = it.serialize()
            length += data.size
            data
        } ?: byteArrayOf()

        val data = ByteArray(length)
        val bb = ByteBuffer.wrap(data)

        bb.put(this.icmpType.toByte())
        bb.put(this.icmpCode.toByte())

        // compute checksum if needed
        if (this.checksum.toInt() == 0) {
            if (parent is IPv6) {
                this.checksum = InternetChecksum.checksumHelper(
                    data.sliceArray(0 until bb.position()),
                    payloadData,
                    parent as IPv6,
                    IpProtocol.IPv6_ICMP
                )
            } else {
                Logger.w("Skipping checksum calculation, no IPv6 parent...")
            }
        }

        bb.putShort(this.checksum)
        bb.put(payloadData)
        return data
    }

    @Throws(PacketParsingException::class)
    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        val bb = ByteBuffer.wrap(data, offset, length)
        icmpType = bb.get().toUByte()
        icmpCode = bb.get().toUByte()
        checksum = bb.short
        payload = when (ICMPv6.MessageType.Companion.from(icmpType)) {
            ICMPv6.MessageType.ECHO_REQUEST -> EchoRequestMessage()
            ICMPv6.MessageType.ECHO_REPLY -> EchoReplyMessage()
            else -> Data()
        }.deserialize(data, bb.position(), bb.limit() - bb.position()).also { parent = this }
        return this
    }

    override fun toString(): String {
        return "ICMPv6(icmpType=${ICMPv6.MessageType.Companion.from(icmpType)
            ?: icmpType}, icmpCode=${ICMPv6.MessageType.Companion.from(icmpCode)
            ?: icmpCode}, checksum=$checksum, payload=$payload)"
    }

    enum class MessageType(val type: UByte, vararg val codes: ICMPv6.MessageCode) {
        DEST_UNREACH(
            1u,
            ICMPv6.MessageCode.NOROUTE,
            ICMPv6.MessageCode.ADM_PROHIBITED,
            ICMPv6.MessageCode.NOT_NEIGHBOUR,
            ICMPv6.MessageCode.ADDR_UNREACH,
            ICMPv6.MessageCode.PORT_UNREACH,
            ICMPv6.MessageCode.POLICY_FAIL,
            ICMPv6.MessageCode.REJECT_ROUTE
        ),
        PKT_TOOBIG(2u),
        TIME_EXCEEDED(
            3u,
            ICMPv6.MessageCode.EXC_HOPLIMIT,
            ICMPv6.MessageCode.EXC_FRAGTIME
        ),
        PARAMPROB(
            4u,
            ICMPv6.MessageCode.HDR_FIELD,
            ICMPv6.MessageCode.UNK_NEXTHDR,
            ICMPv6.MessageCode.UNK_OPTION
        ),
        ECHO_REQUEST(128u),
        ECHO_REPLY(129u),
        MGM_QUERY(130u),
        MGM_REPORT(131u),
        MGM_REDUCTION(132u),
        NI_QUERY(139u),
        NI_REPLY(140u),
        MLD2_REPORT(143u),
        DHAAD_REQUEST(144u),
        DHAAD_REPLY(145u),
        MOBILE_PREFIX_SOL(146u),
        MOBILE_PREFIX_ADV(147u),
        MRDISC_ADV(151u);

        companion object {

            fun from(type: UByte) = values().firstOrNull { it.type == type }
        }
    }

    enum class MessageCode(val parentType: ICMPv6.MessageType, val code: UByte) {
        NOROUTE(ICMPv6.MessageType.DEST_UNREACH, 0u),
        ADM_PROHIBITED(ICMPv6.MessageType.DEST_UNREACH, 1u),
        NOT_NEIGHBOUR(ICMPv6.MessageType.DEST_UNREACH, 2u),
        ADDR_UNREACH(ICMPv6.MessageType.DEST_UNREACH, 3u),
        PORT_UNREACH(ICMPv6.MessageType.DEST_UNREACH, 4u),
        POLICY_FAIL(ICMPv6.MessageType.DEST_UNREACH, 5u),
        REJECT_ROUTE(ICMPv6.MessageType.DEST_UNREACH, 6u),
        EXC_HOPLIMIT(ICMPv6.MessageType.TIME_EXCEEDED, 0u),
        EXC_FRAGTIME(ICMPv6.MessageType.TIME_EXCEEDED, 1u),
        HDR_FIELD(ICMPv6.MessageType.PARAMPROB, 0u),
        UNK_NEXTHDR(ICMPv6.MessageType.PARAMPROB, 1u),
        UNK_OPTION(ICMPv6.MessageType.PARAMPROB, 2u),
    }
}
